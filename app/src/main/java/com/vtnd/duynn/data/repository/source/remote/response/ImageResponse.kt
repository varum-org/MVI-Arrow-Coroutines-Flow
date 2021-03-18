package com.vtnd.duynn.data.repository.source.remote.response

import com.squareup.moshi.Json

/**
 * Created by duynn100198 on 3/17/21.
 */
data class ImageResponse(
    @Json(name = "image_path")
    val imagePath: String
)