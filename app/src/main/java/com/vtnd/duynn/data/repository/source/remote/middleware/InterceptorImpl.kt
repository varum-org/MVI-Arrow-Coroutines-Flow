package com.vtnd.duynn.data.repository.source.remote.middleware

import com.vtnd.duynn.data.repository.source.local.api.SharedPrefApi
import com.vtnd.duynn.data.repository.source.local.api.pref.SharedPrefKey
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

/**
 * Created by duynn100198 on 3/17/21.
 */
class InterceptorImpl(private val sharedPrefApi: SharedPrefApi) : Interceptor {

    private var token by sharedPrefApi.delegate(null as String?, SharedPrefKey.KEY_TOKEN)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val customHeaders = request.headers.values("@")
        val newRequest = when {
            "NoAuth" in customHeaders -> request
            else -> {
                when (val token =
                    runBlocking { token }.also { Timber.d("Current Token $it") }) {
                    null -> request
                    else -> request
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            }
        }

        val response = newRequest.newBuilder()
            .removeHeader("@")
            .build()
            .let(chain::proceed)

        if (response.code in arrayOf(HTTP_UNAUTHORIZED, HTTP_FORBIDDEN)) {
            Timber.d("remove User And Token")
            runBlocking {
                sharedPrefApi.removeKey(SharedPrefKey.KEY_TOKEN)
                sharedPrefApi.removeKey(SharedPrefKey.KEY_USER)
            }
        }
        return response
    }
}
