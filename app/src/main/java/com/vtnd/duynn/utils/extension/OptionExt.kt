package com.vtnd.duynn.utils.extension

import java.util.*

/**
 * Created by duynn100198 on 3/20/21.
 */
sealed class Option<out T> {

    data class Some<T : Any>(val value: T) : Option<T>()

    object None : Option<Nothing>() {
        override fun toString() = "None"
    }
}

inline fun <T, R : Any> Option<T>.map(transform: (T) -> R): Option<R> = when (this) {
    is Option.Some -> Option.Some(transform(value))
    is Option.None -> Option.None
}

fun <A, B> Option<A>.mapNotNull(f: (A) -> B?): Option<B> =
    flatMap { a -> fromNullable(f(a)) }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
        is Option.None -> this
        is Option.Some -> f(value)
    }

fun <A> fromNullable(a: A?): Option<A> = if (a != null) Option.Some(a) else Option.None

/**
 *
 */

inline fun <T, R> Option<T>.fold(ifEmpty: () -> R, ifSome: (T) -> R): R = when (this) {
    is Option.None -> ifEmpty()
    is Option.Some -> ifSome(value)
}

inline fun <T> Option<T>.getOrElse(ifNone: () -> T) = fold(ifNone) { it }

fun <T> Option<T>.getOrNull(): T? = getOrElse { null }

fun <T> Option<T>.getOrThrow(): T = getOrElse { throw NoSuchElementException("No value present") }

/**
 *
 */

fun <T : Any> T?.toOption(): Option<T> = when (this) {
    null -> Option.None
    else -> Option.Some(this)
}
