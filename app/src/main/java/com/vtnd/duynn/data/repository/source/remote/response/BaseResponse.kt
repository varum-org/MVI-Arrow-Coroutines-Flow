package com.vtnd.duynn.data.repository.source.remote.response

import com.squareup.moshi.Json
import com.vtnd.duynn.data.error.AppError

/**
 * Created by duynn100198 on 3/17/21.
 */
data class BaseResponse<Data : Any>(
    @Json(name = "data") private val data: Data,
    @Json(name = "messages") val messages: String,
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "success") val success: Boolean
) {
    fun unwrap(): Data {
        return if (success) data
        else throw AppError.Remote.ServerError(
            errorMessage = messages,
            statusCode = statusCode
        )
    }
}
