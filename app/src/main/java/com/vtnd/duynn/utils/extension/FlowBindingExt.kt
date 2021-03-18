package com.vtnd.duynn.utils.extension

import android.view.View
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by duynn100198 on 3/18/21.
 */

fun <T> SendChannel<T>.safeOffer(element: T): Boolean {
    return runCatching { offer(element) }.getOrDefault(false)
}

@ExperimentalCoroutinesApi
fun EditText.firstChange(): Flow<Unit> {
    return callbackFlow {
        val listener = doOnTextChanged { _, _, _, _ -> safeOffer(Unit) }
        awaitClose {
            Dispatchers.Main.dispatch(EmptyCoroutineContext) {
                removeTextChangedListener(listener)
                Timber.d("removeTextChangedListener $listener ${this@firstChange}")
            }
        }
    }.take(1)
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ ->
            safeOffer(text) }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

@ExperimentalCoroutinesApi
@CheckResult
fun View.clicks(): Flow<View> {
    return callbackFlow {
        setOnClickListener { safeOffer(it) }
        awaitClose { setOnClickListener(null) }
    }.conflate()
}
