package com.vtnd.duynn.data.repository

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.Option
import arrow.core.extensions.fx
import com.vtnd.duynn.data.error.AppError
import com.vtnd.duynn.data.error.DomainResult
import com.vtnd.duynn.data.error.rightResult
import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.data.repository.source.UserDataSource
import com.vtnd.duynn.data.repository.source.remote.body.RegisterBody
import com.vtnd.duynn.domain.repository.UserRepository
import com.vtnd.duynn.domain.scheduler.AppDispatchers.IO
import com.vtnd.duynn.domain.scheduler.DispatchersProvider
import com.vtnd.duynn.presentation.mapper.ErrorMapper
import com.vtnd.duynn.utils.extension.catchError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

/**
 * Created by duynn100198 on 3/17/21.
 */
@KoinApiExtension
class UserRepositoryImpl(
    private val userRemoteSource: UserDataSource.Remote,
    private val mapper: ErrorMapper,
    private val userLocalSource: UserDataSource.Local,
) : UserRepository, KoinComponent {
    private val dispatchersProvider = get<DispatchersProvider>(named(IO))
    private val checkAuthDeferred = CompletableDeferred<Unit>()

    init {
        CoroutineScope(dispatchersProvider.dispatcher()).launch {
            while (isActive) {
                checkAuthInternal()
                delay(CHECK_AUTH_INTERVAL)
            }
        }
    }

    private val userObservable: Flow<Either<AppError, Option<UserData>>> =
        userLocalSource.userObservable()
            .combine(userLocalSource.tokenObservable()) { userOptional, tokenOptional ->
                Option.fx {
                    !tokenOptional
                    val user = !userOptional
                    user
                }.rightResult()
            }.catchError(mapper)
            .distinctUntilChanged()
            .buffer(1)

    override suspend fun login(
        email: String,
        password: String
    ): DomainResult<Unit?> {
        return catch {
            val (user, auth_token) =
                userRemoteSource
                    .login(email, password, "deviceToken")
                    .unwrap()
                    .also { Timber.d("Login User { email: $email pass: $password }") }
            auth_token?.let { userLocalSource.saveAuthToken(it) }
            val id = user?.id
                ?: return@catch
            userRemoteSource.getUser(id)
                .unwrap()
                .let {
                    userLocalSource.saveUser(it)
                }
        }.mapLeft(mapper::map)
    }

    override suspend fun register(user: RegisterBody): DomainResult<Any> {
        return catch {
            userRemoteSource.register(user)
                .unwrap()
                .also { Timber.d("Register User") }
        }.mapLeft(mapper::map)
    }

    override suspend fun getAllUser(): DomainResult<List<UserData>> {
        return catch {
            withContext(dispatchersProvider.dispatcher()) {
                userRemoteSource.getAllUser().unwrap()
                    .also { Timber.d("Get Users ") }
            }
        }.mapLeft(mapper::map)
    }

    override suspend fun logout(): DomainResult<Any> {
        return catch {
            userRemoteSource.logout("token")
                .unwrap()
                .also { Timber.d("Logout") }
            userLocalSource.removeUserAndToken()
        }.mapLeft(mapper::map)
    }

    override fun userObservable() = userObservable

    override suspend fun checkAuth(): DomainResult<Boolean> {
        return catch {
            checkAuthDeferred.await()
            userLocalSource.token() !== null && userLocalSource.user() !== null
        }.mapLeft(mapper::map)
    }

    override suspend fun checkAuthInternal() {
        try {
            Timber.d("[CHECK AUTH] started")
            userLocalSource.token()
                ?: return userLocalSource.removeUserAndToken()
            val id = userLocalSource.user()?.id
                ?: return userLocalSource.removeUserAndToken()
            userRemoteSource.getUser(id)
                .unwrap()
                .let {
                    userLocalSource.saveUser(it)
                }
            Timber.d("[CHECK AUTH] success")
        } catch (e: Exception) {
            Timber.d(e, "[CHECK AUTH] failure: $e")
            if ((e as? HttpException)?.code() in arrayOf(
                    HttpURLConnection.HTTP_UNAUTHORIZED,
                    HttpURLConnection.HTTP_FORBIDDEN
                )
            ) {
                userLocalSource.removeUserAndToken()
                Timber.d(e, "[CHECK AUTH] Login again!")
            }
        } finally {
            checkAuthDeferred.complete(Unit)
        }
    }

    override suspend fun editUser(
        id: String,
        userName: String,
        phone: String,
        avatarUri: Uri?
    ): DomainResult<Any> {
        return catch {
            withContext((dispatchersProvider.dispatcher())) {
                val response = userRemoteSource.editUser(id, userName, phone, avatarUri)
                if (response.success) {
                    userRemoteSource.getUser(id)
                        .unwrap()
                        .let {
                            userLocalSource.saveUser(it)
                        }
                }
                response.unwrap()
            }
        }.mapLeft(mapper::map)
    }

    override suspend fun sendCode(email: String): DomainResult<Any> {
        return catch {
            withContext(dispatchersProvider.dispatcher()) {
                userRemoteSource.sendCode(email).unwrap()
            }
        }.mapLeft(mapper::map)
    }

    override suspend fun checkCode(code: Int, password: String): DomainResult<Any> {
        return catch {
            withContext(dispatchersProvider.dispatcher()) {
                userRemoteSource.checkCode(code, password).unwrap()
            }
        }.mapLeft(mapper::map)
    }

    companion object {
        const val CHECK_AUTH_INTERVAL = 180_000L // 3 minutes
    }
}
