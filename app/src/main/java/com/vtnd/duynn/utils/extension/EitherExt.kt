@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package com.vtnd.duynn.utils.extension

/**
 * Created by duynn100198 on 3/18/21.
 */
sealed class Either<out L, out R> {
    internal abstract val isLeft: Boolean
    internal abstract val isRight: Boolean

    fun isLeft(): Boolean = isLeft

    fun isRight(): Boolean = isRight

    data class Left<out T>(val value: T) : Either<T, Nothing>() {
        override val isLeft
            get() = true
        override val isRight
            get() = false
    }

    data class Right<out R>(val value: R) : Either<Nothing, R>() {
        override val isLeft
            get() = false
        override val isRight
            get() = true
    }
}

fun <T : Any?> T.leftResult(): Either<T, Nothing> = Either.Left(this)
fun <T : Any?> T.rightResult(): Either<Nothing, T> = Either.Right(this)

fun <L : Any?, R : Any?> Either<L, R>.getOrNull(): R? {
    return when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }
}

fun <L : Throwable, R : Any?> Either<L, R>.getOrThrow(): R {
    return when (this) {
        is Either.Left -> throw value
        is Either.Right -> value
    }
}

inline fun <L, R, T> Either<L, R>.fold(ifLeft: (L) -> T, ifRight: (R) -> T): T =
    when (this) {
        is Either.Left -> ifLeft(value)
        is Either.Right -> ifRight(value)
    }

inline fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> =
    fold({ this as Either.Left }, f)

inline fun <L, R, T> Either<L, R>.map(f: (R) -> T): Either<L, T> =
    flatMap { Either.Right(f(it)) }

fun <A, B> Either<A, B>.leftOrNull(): A? = this.fold(
    ifLeft = {
        return it
    },
    ifRight = {
        return null
    }
)

fun <A, B> Either<A, B>.rightOrNull(): B? = this.fold(
    ifLeft = {
        return null
    },
    ifRight = {
        return it
    }
)

/**
 * Map over Left and Right of this Either
 */
fun <L, R, C, D> Either<L, R>.bimap(
    leftOperation: (L) -> C,
    rightOperation: (R) -> D,
): Either<C, D> = fold(
    { Either.Left(leftOperation(it)) },
    { Either.Right(rightOperation(it)) }
)

fun <A> identity(a: A): A = a

suspend fun <R> catch(f: suspend () -> R): Either<Throwable, R> =
    catch(::identity, f)

suspend fun <L, R> catch(fe: (Throwable) -> L, f: suspend () -> R): Either<L, R> =
    try {
        f().rightResult()
    } catch (t: Throwable) {
        fe(t.nonFatalOrThrow()).leftResult()
    }

fun nonFatal(t: Throwable): Boolean =
    when (t) {
        is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> false
        else -> true
    }

fun Throwable.nonFatalOrThrow(): Throwable =
    if (nonFatal(this)) this else throw this

fun <A, B, C> Either<A, B>.mapLeft(f: (A) -> C): Either<C, B> =
    fold({ Either.Left(f(it)) }, { Either.Right(it) })
