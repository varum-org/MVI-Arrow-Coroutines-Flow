package com.vtnd.duynn.data.repository.source.local.api.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import arrow.core.Option
import arrow.core.toOption
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vtnd.duynn.data.repository.source.local.api.SharedPrefApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by duynn100198 on 3/17/21.
 */
@Suppress("UNCHECKED_CAST")
class SharedPrefApiImpl(private val context: Context, private val moshi: Moshi) : SharedPrefApi {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SharedPrefKey.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun <T> delegate(
        defaultValue: T,
        key: String?,
        commit: Boolean
    ): ReadWriteProperty<Any, T> {
        return when (defaultValue) {
            is String? -> delegate(
                SharedPreferences::getString,
                SharedPreferences.Editor::putString,
                defaultValue,
                key,
                commit
            )
            is Set<*>? -> delegate(
                SharedPreferences::getStringSet,
                SharedPreferences.Editor::putStringSet,
                defaultValue?.filterIsInstanceTo(mutableSetOf<String>()),
                key,
                commit
            )
            is Boolean -> delegate(
                SharedPreferences::getBoolean,
                SharedPreferences.Editor::putBoolean,
                defaultValue,
                key,
                commit
            )
            is Int -> delegate(
                SharedPreferences::getInt,
                SharedPreferences.Editor::putInt,
                defaultValue,
                key,
                commit
            )
            is Long -> delegate(
                SharedPreferences::getLong,
                SharedPreferences.Editor::putLong,
                defaultValue,
                key,
                commit
            )
            is Float -> delegate(
                SharedPreferences::getFloat,
                SharedPreferences.Editor::putFloat,
                defaultValue,
                key,
                commit
            )
            else -> {
                error("Not support for type clazz")
            }
        } as ReadWriteProperty<Any, T>
    }

    @ExperimentalCoroutinesApi
    override fun observeString(key: String, defValue: String?): Flow<Option<String>> =
        observeKey(key, defValue)

    override fun <T> putList(key: String, clazz: Class<T>, list: List<T>) {
        val listMyData = Types.newParameterizedType(MutableList::class.java, clazz)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listMyData)
        sharedPreferences.edit().putString(key, adapter.toJson(list)).apply()
    }

    override fun <T> getList(key: String, clazz: Class<T>): List<T>? {
        val listMyData = Types.newParameterizedType(MutableList::class.java, clazz)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listMyData)
        return getValue(key, null as String?)?.let { adapter.fromJson(it) }
    }

    override fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    private inline fun <T> delegate(
        crossinline getter: SharedPreferences.(key: String, defaultValue: T) -> T,
        crossinline setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor,
        defaultValue: T,
        key: String? = null,
        commit: Boolean = false
    ): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            sharedPreferences.getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
            sharedPreferences.edit(commit) { setter(key ?: property.name, value) }
    }

    @ExperimentalCoroutinesApi
    private inline fun <reified T> observeKey(
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
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                Timber.d("close share preferences listener")
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private inline fun <reified T> getValue(
        key: String,
        default: T?
    ): T? = when (val clazz = T::class) {
        String::class -> sharedPreferences.getString(key, default as String?)
        Set::class -> sharedPreferences.getStringSet(
            key,
            (default as Set<*>?)?.filterIsInstanceTo(LinkedHashSet())
        )
        Boolean::class -> sharedPreferences.getBoolean(key, default as Boolean)
        Int::class -> sharedPreferences.getInt(key, default as Int)
        Long::class -> sharedPreferences.getLong(key, default as Long)
        Float::class -> sharedPreferences.getFloat(key, default as Float)
        else -> error("Not support for type $clazz")
    } as T?
}
