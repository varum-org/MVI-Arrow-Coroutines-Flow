package com.vtnd.duynn.data.repository.source.remote.response

import com.squareup.moshi.Json

/**
 * Created by duynn100198 on 3/17/21.
 */
data class LoginResponse(
    @Json(name = "user") val user: UserInfo?,
    @Json(name = "auth_token") val token: String?,
    @Json(name = "is_admin") val isAdmin: Boolean?
) {
    data class UserInfo(
        @Json(name = "user_name")
        val userName: String?,
        @Json(name = "email")
        val email: String? ,
        @Json(name = "role_id")
        val roleId: Int?,
        @Json(name = "image_path")
        val imagePath: String? ,
        @Json(name = "_id")
        val id: String?
    )
}
