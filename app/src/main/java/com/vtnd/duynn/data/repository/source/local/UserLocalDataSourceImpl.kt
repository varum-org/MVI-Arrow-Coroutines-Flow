package com.vtnd.duynn.data.repository.source.local

import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.data.model.UserDataJsonAdapter
import com.vtnd.duynn.data.repository.source.UserDataSource
import com.vtnd.duynn.data.repository.source.local.api.SharedPrefApi
import com.vtnd.duynn.data.repository.source.local.api.pref.SharedPrefKey.KEY_TOKEN
import com.vtnd.duynn.data.repository.source.local.api.pref.SharedPrefKey.KEY_USER
import com.vtnd.duynn.domain.scheduler.DispatchersProvider
import com.vtnd.duynn.domain.scheduler.AppDispatchers.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
@KoinApiExtension
@ExperimentalCoroutinesApi
class UserLocalDataSourceImpl(
    sharedPrefApi: SharedPrefApi,
    private val userLocalJsonAdapter: UserDataJsonAdapter
) : UserDataSource.Local, KoinComponent {
    private val dispatchersProvider = get<DispatchersProvider>(named(IO))
    private var token by sharedPrefApi.delegate(null as String?, KEY_TOKEN, commit = true)
    private var userLocal by sharedPrefApi.delegate(null as String?, KEY_USER, commit = true)

    private var userObservable = sharedPrefApi.observeString(KEY_USER)
        .flowOn(dispatchersProvider.dispatcher())
        .map { json -> json.mapNotNull { it.toUserLocal() } }
        .buffer(1)
        .also { Timber.i("User $it") }

    private var tokenObservable = sharedPrefApi.observeString(KEY_TOKEN)
        .flowOn(dispatchersProvider.dispatcher())
        .map { it }
        .buffer(1)
        .also { Timber.i("Token $it") }

    override suspend fun saveAuthToken(token: String) =
        withContext(Dispatchers.IO) { this@UserLocalDataSourceImpl.token = token }

    override fun tokenObservable() = tokenObservable

    override suspend fun token() = withContext(dispatchersProvider.dispatcher()) { token }

    override suspend fun user() =
        withContext(dispatchersProvider.dispatcher()) { userLocal.toUserLocal() }

    override suspend fun saveUser(user: UserData) = withContext(dispatchersProvider.dispatcher()) {
        this@UserLocalDataSourceImpl.userLocal = userLocalJsonAdapter.toJson(user)
    }

    override fun userObservable() = userObservable

    override suspend fun removeUserAndToken() = withContext(dispatchersProvider.dispatcher()) {
        userLocal = null
        token = null
        Timber.i("remove User And Token")
    }

    private fun String?.toUserLocal(): UserData? =
        runCatching { userLocalJsonAdapter.fromJson(this ?: return null) }.getOrNull()
}
