package com.vtnd.duynn.utils.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Created by duynn100198 on 3/19/21.
 */
/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T : Any>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

inline fun <T : Any> LiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit,
) = Observer<T?> { it?.let(observer) }
    .also { observe(owner, it) }

inline fun <T : Any> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit,
) = Observer { event: Event<T>? ->
    event
        ?.getContentIfNotHandled()
        ?.let(observer)
}.also { observe(owner, it) }

@Suppress("NOTHING_TO_INLINE")
inline fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
