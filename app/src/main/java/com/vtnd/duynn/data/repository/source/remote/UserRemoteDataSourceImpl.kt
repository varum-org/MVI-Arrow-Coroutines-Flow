package com.vtnd.duynn.data.repository.source.remote

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.data.repository.source.UserDataSource
import com.vtnd.duynn.data.repository.source.remote.api.ApiService
import com.vtnd.duynn.data.repository.source.remote.body.RegisterBody
import com.vtnd.duynn.data.repository.source.remote.body.UpdateProfileBody
import com.vtnd.duynn.data.repository.source.remote.response.BaseResponse
import com.vtnd.duynn.data.repository.source.remote.response.LoginResponse
import com.vtnd.duynn.domain.scheduler.DispatchersProvider
import com.vtnd.duynn.domain.scheduler.AppDispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import timber.log.Timber
import java.io.ByteArrayOutputStream

/**
 * Created by duynn100198 on 3/17/21.
 */
@KoinApiExtension
class UserRemoteDataSourceImpl(
    private val apiService: ApiService,
    private val application: Application
) : UserDataSource.Remote, KoinComponent {
    private val dispatchersProvider = get<DispatchersProvider>(named(IO))

    override suspend fun login(
        email: String,
        password: String,
        deviceToken: String
    ): BaseResponse<LoginResponse> {
        Timber.i("login")
        return withContext(dispatchersProvider.dispatcher()) {
            apiService.login(
                email,
                password,
                deviceToken
            )
        }
    }

    override suspend fun register(user: RegisterBody): BaseResponse<Any> {
        return withContext(dispatchersProvider.dispatcher()) { apiService.register(user) }
    }

    override suspend fun getAllUser(): BaseResponse<List<UserData>> {
        return withContext(dispatchersProvider.dispatcher()) { apiService.getAllUser() }
    }

    override suspend fun logout(deviceToken: String): BaseResponse<Any> {
        return withContext(dispatchersProvider.dispatcher()) { apiService.logout(deviceToken) }
    }

    override suspend fun getUser(id: String): BaseResponse<UserData> {
        return withContext(dispatchersProvider.dispatcher()) { apiService.getUser(id) }
    }

    override suspend fun editUser(
        id: String,
        userName: String,
        phone: String,
        avatarUri: Uri?
    ): BaseResponse<Any> {
        return withContext(dispatchersProvider.dispatcher()) {
            apiService.editUser(
                id,
                UpdateProfileBody(
                    userName = userName,
                    phone = phone,
                    imagePath = uploadAvatar(avatarUri)
                )
            )
        }
    }

    override suspend fun sendCode(email: String): BaseResponse<Any> {
        return withContext(dispatchersProvider.dispatcher()) {
            apiService.sendCode(email)
        }
    }

    override suspend fun checkCode(code: Int, password: String): BaseResponse<Any> {
        return withContext(dispatchersProvider.dispatcher()){
            apiService.checkCode(code, password)
        }
    }

    private suspend fun uploadAvatar(image: Uri?): String? {
        return withContext(dispatchersProvider.dispatcher()) {
            if (image != null) {
                val contentResolver = application.contentResolver
                val type = contentResolver.getType(image)
                val inputStream = contentResolver.openInputStream(image)
                val fileName = contentResolver.query(image, null, null, null, null)!!.use {
                    it.moveToFirst()
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
                val bytes = ByteArrayOutputStream().use {
                    inputStream?.copyTo(it)
                    it.toByteArray()
                }
                val requestFile = bytes.toRequestBody(type?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", fileName, requestFile)
                apiService.uploadImage(body).unwrap().imagePath
            } else {
                null
            }
        }
    }
}
