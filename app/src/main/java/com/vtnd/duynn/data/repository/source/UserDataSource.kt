package com.vtnd.duynn.data.repository.source

import android.net.Uri
import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.data.repository.source.remote.body.RegisterBody
import com.vtnd.duynn.data.repository.source.remote.response.BaseResponse
import com.vtnd.duynn.data.repository.source.remote.response.LoginResponse
import com.vtnd.duynn.utils.extension.Option
import kotlinx.coroutines.flow.Flow

/**
 * Created by duynn100198 on 3/17/21.
 */
interface UserDataSource {

    interface Local {
        /*
        token
        */
        suspend fun token(): String?
        suspend fun saveAuthToken(token: String)
        fun tokenObservable(): Flow<Option<String>>

        /*
        user local
         */
        suspend fun user(): UserData?
        suspend fun saveUser(user: UserData)
        fun userObservable(): Flow<Option<UserData>>
        suspend fun removeUserAndToken()

    }

    interface Remote {
        suspend fun login(
            email: String,
            password: String,
            deviceToken: String
        ): BaseResponse<LoginResponse>
        suspend fun register(user: RegisterBody): BaseResponse<Any>
        suspend fun getAllUser(): BaseResponse<List<UserData>>
        suspend fun logout(deviceToken: String): BaseResponse<Any>
        suspend fun getUser(id: String): BaseResponse<UserData>
        suspend fun editUser(
            id: String,
            userName: String,
            phone: String,
            avatarUri: Uri?
        ): BaseResponse<Any>
        suspend fun sendCode(email: String): BaseResponse<Any>
        suspend fun checkCode(
            code: Int,
            password: String
        ): BaseResponse<Any>
    }
}
