package com.vtnd.duynn.data.repository.source.local.api

import arrow.core.Option
import kotlinx.coroutines.flow.Flow
import kotlin.properties.ReadWriteProperty

/**
 * Created by duynn100198 on 3/17/21.
 */
interface SharedPrefApi {

    fun <T : Any?> delegate(
        defaultValue: T,
        key: String? = null,
        commit: Boolean = false
    ): ReadWriteProperty<Any, T>

    fun observeString(key: String, defValue: String? = null): Flow<Option<String>>
    fun <T> putList(key: String, clazz: Class<T>, list: List<T>)
    fun <T> getList(key: String, clazz: Class<T>): List<T>?
    fun removeKey(key: String)
    fun clear()
}
