package com.vtnd.duynn.data.repository.source.remote.body

import com.squareup.moshi.Json

/**
 * Created by duynn100198 on 3/17/21.
 */
data class RegisterBody(
    @Json(name = "user_name")
    val userName: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "password")
    val password: String? ,
    @Json(name = "role_id")
    val roleId: Int?
)