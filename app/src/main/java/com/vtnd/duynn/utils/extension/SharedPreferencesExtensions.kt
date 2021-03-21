package com.vtnd.duynn.utils.extension

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by duynn100198 on 3/20/21.
 */
inline fun <T> SharedPreferences.delegate(
    crossinline getter: SharedPreferences.(key: String, defaultValue: T) -> T,
    crossinline setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor,
    defaultValue: T,
    key: String? = null,
    commit: Boolean = false
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        getter(key ?: property.name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
        edit(commit) { setter(key ?: property.name, value) }
}

@ExperimentalCoroutinesApi
inline fun <reified T> SharedPreferences.observeKey(
    key: String,
    default: T?
): Flow<Option<T>> {
    return callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                offer(getValue(key, default).toOption())
                Timber.d("change share preferences listener")
            }
        }
        send(getValue(key, default).toOption())
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            Timber.d("close share preferences listener")
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<Option<T>>.unwrap(defValue: T): Flow<T> =
    map { it.getOrElse { defValue } }

@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T> SharedPreferences.getValue(
    key: String,
    default: T?
): T? = when (val clazz = T::class) {
    String::class -> getString(key, default as String?)
    Set::class -> getStringSet(
        key, (default as Set<*>?)?.filterIsInstanceTo(LinkedHashSet())
    )
    Boolean::class -> getBoolean(key, default as Boolean)
    Int::class -> getInt(key, default as Int)
    Long::class -> getLong(key, default as Long)
    Float::class -> getFloat(key, default as Float)
    else -> error("Not support for type $clazz")
} as T?