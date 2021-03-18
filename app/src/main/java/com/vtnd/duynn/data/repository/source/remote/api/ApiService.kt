package com.vtnd.duynn.data.repository.source.remote.api

import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.data.repository.source.remote.body.RegisterBody
import com.vtnd.duynn.data.repository.source.remote.body.UpdateProfileBody
import com.vtnd.duynn.data.repository.source.remote.response.BaseResponse
import com.vtnd.duynn.data.repository.source.remote.response.ImageResponse
import com.vtnd.duynn.data.repository.source.remote.response.LoginResponse
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.*

/**
 * Created by duynn100198 on 3/17/21.
 */
interface ApiService {
    @Headers("@: NoAuth")
    @FormUrlEncoded
    @POST("api/user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") deviceToken: String
    ): BaseResponse<LoginResponse>

    @Headers("@: NoAuth")
    @POST("api/user/register")
    suspend fun register(@Body user: RegisterBody): BaseResponse<Any>

    @GET("api/user/all")
    suspend fun getAllUser(): BaseResponse<List<UserData>>

    @PUT("api/user/logout")
    @FormUrlEncoded
    suspend fun logout(
        @Field("device_token") deviceToken: String
    ): BaseResponse<Any>

    @POST("api/user/detail/{id}")
    suspend fun getUser(@Path("id") id: String): BaseResponse<UserData>

    @POST("api/user/edit/{id}")
    suspend fun editUser(
        @Path("id") id: String,
        @Body updateProfileBody: UpdateProfileBody
    ): BaseResponse<Any>

    @POST("api/user/forget")
    @FormUrlEncoded
    suspend fun sendCode(
        @Field("email") email: String
    ): BaseResponse<Any>

    @POST("api/user/reset/{code}")
    @FormUrlEncoded
    suspend fun checkCode(
        @Path("code") code: Int,
        @Field("password") password: String
    ): BaseResponse<Any>

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(@Part body: MultipartBody.Part): BaseResponse<ImageResponse>

    companion object Factory {
        operator fun invoke(retrofit: Retrofit) = retrofit.create<ApiService>()
    }
}
