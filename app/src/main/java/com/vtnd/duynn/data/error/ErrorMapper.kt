package com.vtnd.duynn.data.error

import android.database.sqlite.SQLiteException
import com.vtnd.duynn.data.repository.source.remote.response.ErrorResponseJsonAdapter
import com.vtnd.duynn.utils.extension.leftResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by duynn100198 on 3/17/21.
 */
class ErrorMapper(private val errorResponseJsonAdapter: ErrorResponseJsonAdapter) {
    /**
     * Transform [throwable] to [AppError]
     */
    fun map(throwable: Throwable): AppError {
        return when (throwable) {
            is AppError -> throwable
            is SQLiteException -> {
                AppError.Local.DatabaseError(throwable)
            }
            is IOException -> {
                when (throwable) {
                    is UnknownHostException -> AppError.Remote.NetworkError(throwable)
                    is SocketTimeoutException -> AppError.Remote.NetworkError(throwable)
                    is SocketException -> AppError.Remote.NetworkError(throwable)
                    else -> AppError.UnexpectedError(
                        cause = throwable,
                        errorMessage = "Unknown IOException: $throwable"
                    )
                }
            }
            is HttpException -> {
                throwable.response()!!
                    .takeUnless { it.isSuccessful }!!
                    .errorBody()!!
                    .use { body ->
                        body.use { errorResponseJsonAdapter.fromJson(it.string()) }!!
                    }
                    .let { response ->
                        AppError.Remote.ServerError(
                            errorMessage = response.messages,
                            statusCode = response.statusCode,
                            cause = throwable
                        )
                    }
            }
            else -> {
                AppError.UnexpectedError(
                    cause = throwable,
                    errorMessage = "Unknown Throwable: $throwable"
                )
            }
        }
    }

    /**
     * Transform [throwable] to left branch of [DomainResult]
     */
    fun mapAsLeft(throwable: Throwable) = map(throwable).leftResult()
}
