package com.vtnd.duynn.utils.extension

import com.vtnd.duynn.data.error.AppError

/**
 * Created by duynn100198 on 3/20/21.
 */
fun AppError.getMessage(): String {
    return when (this) {
        is AppError.Remote.NetworkError -> "Network error"
        is AppError.Remote.ServerError -> errorMessage
        is AppError.Local.DatabaseError -> "Database error"
        is AppError.UnexpectedError -> "Unexpected error $errorMessage"
        AppError.LocationError.TimeoutGetCurrentLocation -> "Timeout to get current location. Please try again!"
        is AppError.LocationError.LocationSettingsDisabled -> "Location settings disabled. Please enable to continue!"
        AppError.LocationError.GeocoderEmptyResult -> "Cannot get address from coordinates"
        AppError.WrongRole -> "This app only supports doctor roles"
    }
}
