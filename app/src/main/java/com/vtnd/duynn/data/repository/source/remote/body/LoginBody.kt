package com.vtnd.duynn.data.repository.source.remote.body

import com.squareup.moshi.Json

/**
 * Created by duynn100198 on 3/18/21.
 */
data class LoginBody(
    @Json(name = "email")
    val email: String?,
    @Json(name = "password")
    val password: String?
)
