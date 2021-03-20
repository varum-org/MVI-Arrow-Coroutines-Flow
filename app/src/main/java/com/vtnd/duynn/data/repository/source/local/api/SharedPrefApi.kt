package com.vtnd.duynn.data.repository.source.local.api

import com.vtnd.duynn.utils.extension.Option
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
    fun observeStringSet(key: String, defValue: Set<String>? = null): Flow<Option<Set<String>>>
    fun observeBoolean(key: String, defValue: Boolean = false): Flow<Boolean>
    fun observeInt(key: String, defValue: Int = 0): Flow<Int>
    fun observeLong(key: String, defValue: Long = 0L): Flow<Long>
    fun observeFloat(key: String, defValue: Float = 0f): Flow<Float>
    fun <T> putList(key: String, clazz: Class<T>, list: List<T>)
    fun <T> getList(key: String, clazz: Class<T>): List<T>?
    fun removeKey(key: String)
    fun clear()
}
