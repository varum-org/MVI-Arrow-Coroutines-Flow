package com.vtnd.duynn.data.repository.source.remote.body

import com.squareup.moshi.Json

/**
 * Created by duynn100198 on 3/17/21.
 */
data class UpdateProfileBody(
    @Json(name = "user_name")
    val userName: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "image_path")
    val imagePath: String?
)
