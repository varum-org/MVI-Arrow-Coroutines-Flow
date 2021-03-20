package com.vtnd.duynn.data.error

/**
 * Created by duynn100198 on 3/17/21.
 */
sealed class AppError(cause: Throwable?) : Throwable(cause) {
    object WrongRole : AppError(null)

    sealed class Remote(cause: Throwable?) : AppError(cause) {
        data class NetworkError(override val cause: Throwable) : Remote(cause)

        data class ServerError(
            val errorMessage: String,
            val statusCode: Int,
            override val cause: Throwable? = null
        ) : Remote(cause)

    }

    sealed class Local(cause: Throwable?) : AppError(cause) {
        data class DatabaseError(override val cause: Throwable) : AppError(cause)
    }

    data class UnexpectedError(
        val errorMessage: String,
        override val cause: Throwable
    ) : AppError(cause)

    sealed class LocationError(cause: Throwable?) : AppError(cause) {
        object TimeoutGetCurrentLocation : LocationError(null)
        data class LocationSettingsDisabled(val throwable: Throwable) : LocationError(throwable)
        object GeocoderEmptyResult : LocationError(null)
    }
}
