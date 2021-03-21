package com.vtnd.duynn.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vtnd.duynn.BuildConfig
import com.vtnd.duynn.data.error.ErrorMapper
import com.vtnd.duynn.data.model.UserDataJsonAdapter
import com.vtnd.duynn.data.repository.source.local.api.SharedPrefApi
import com.vtnd.duynn.data.repository.source.remote.api.ApiService
import com.vtnd.duynn.data.repository.source.remote.middleware.InterceptorImpl
import com.vtnd.duynn.data.repository.source.remote.response.ErrorResponseJsonAdapter
import com.vtnd.duynn.utils.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by duynn100198 on 3/17/21.
 */
private fun provideMoshi(): Moshi {
    return Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
}

private fun provideErrorResponseJsonAdapter(moshi: Moshi): ErrorResponseJsonAdapter {
    return ErrorResponseJsonAdapter(moshi)
}

private fun provideErrorMapper(adapter: ErrorResponseJsonAdapter): ErrorMapper {
    return ErrorMapper(adapter)
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}

private fun provideUserLocalJsonAdapter(moshi: Moshi): UserDataJsonAdapter {
    return UserDataJsonAdapter(moshi)
}

private fun provideAuthInterceptor(sharedPrefApi: SharedPrefApi): InterceptorImpl {
    return InterceptorImpl(sharedPrefApi)
}

private fun provideRetrofit(moshi: Moshi, client: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()
}

private fun provideOkHttpClient(
    interceptorImpl: InterceptorImpl
): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
        )
        .addInterceptor(interceptorImpl)
        .build()
}

private fun provideApiService(retrofit: Retrofit): ApiService = ApiService(retrofit)

val networkModule = module {
    single { provideMoshi() }
    factory { provideAuthInterceptor(get()) }
    factory { provideErrorResponseJsonAdapter(get()) }
    factory { provideUserLocalJsonAdapter(get()) }
    single { provideErrorMapper(get()) }
    single { provideSharedPreferences(androidApplication()) }
    single { provideRetrofit(get(), get(), get(named(Constants.KEY_BASE_URL))) }
    single { provideOkHttpClient(get()) }
    single { provideApiService(get()) }
    // Other APIs
}
