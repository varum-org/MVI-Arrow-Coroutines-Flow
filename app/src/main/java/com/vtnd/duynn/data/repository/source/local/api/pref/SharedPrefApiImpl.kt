package com.vtnd.duynn.data.repository.source.local.api.pref

import android.content.Context
import android.content.SharedPreferences
import arrow.core.Option
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vtnd.duynn.data.repository.source.local.api.SharedPrefApi
import com.vtnd.duynn.utils.extension.delegate
import com.vtnd.duynn.utils.extension.getValue
import com.vtnd.duynn.utils.extension.observeKey
import com.vtnd.duynn.utils.extension.unwrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.properties.ReadWriteProperty

/**
 * Created by duynn100198 on 3/17/21.
 */
@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class SharedPrefApiImpl(private val context: Context, private val moshi: Moshi) : SharedPrefApi {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SharedPrefKey.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun <T> delegate(
        defaultValue: T,
        key: String?,
        commit: Boolean
    ): ReadWriteProperty<Any, T> {
        return when (defaultValue) {
            is String? -> sharedPreferences.delegate(
                SharedPreferences::getString,
                SharedPreferences.Editor::putString,
                defaultValue,
                key,
                commit
            )
            is Set<*>? -> sharedPreferences.delegate(
                SharedPreferences::getStringSet,
                SharedPreferences.Editor::putStringSet,
                defaultValue?.filterIsInstanceTo(mutableSetOf<String>()),
                key,
                commit
            )
            is Boolean -> sharedPreferences.delegate(
                SharedPreferences::getBoolean,
                SharedPreferences.Editor::putBoolean,
                defaultValue,
                key,
                commit
            )
            is Int -> sharedPreferences.delegate(
                SharedPreferences::getInt,
                SharedPreferences.Editor::putInt,
                defaultValue,
                key,
                commit
            )
            is Long -> sharedPreferences.delegate(
                SharedPreferences::getLong,
                SharedPreferences.Editor::putLong,
                defaultValue,
                key,
                commit
            )
            is Float -> sharedPreferences.delegate(
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

    override fun observeString(key: String, defValue: String?): Flow<Option<String>> =
        sharedPreferences.observeKey(key, defValue)

    override fun observeStringSet(key: String, defValue: Set<String>?): Flow<Option<Set<String>>> =
        sharedPreferences.observeKey(key, defValue)

    override fun observeBoolean(key: String, defValue: Boolean): Flow<Boolean> =
        sharedPreferences.observeKey(key, defValue).unwrap(defValue)

    override fun observeInt(key: String, defValue: Int): Flow<Int>  =
        sharedPreferences.observeKey(key, defValue).unwrap(defValue)

    override fun observeLong(key: String, defValue: Long): Flow<Long>  =
        sharedPreferences.observeKey(key, defValue).unwrap(defValue)

    override fun observeFloat(key: String, defValue: Float): Flow<Float>  =
        sharedPreferences.observeKey(key, defValue).unwrap(defValue)

    override fun <T> putList(key: String, clazz: Class<T>, list: List<T>) {
        val listMyData = Types.newParameterizedType(MutableList::class.java, clazz)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listMyData)
        sharedPreferences.edit().putString(key, adapter.toJson(list)).apply()
    }

    override fun <T> getList(key: String, clazz: Class<T>): List<T>? {
        val listMyData = Types.newParameterizedType(MutableList::class.java, clazz)
        val adapter: JsonAdapter<List<T>> = moshi.adapter(listMyData)
        return sharedPreferences.getValue(key, null as String?)?.let { adapter.fromJson(it) }
    }

    override fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
