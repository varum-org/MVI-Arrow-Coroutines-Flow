@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package com.vtnd.duynn.utils.extension

import arrow.core.Either

/**
 * Created by duynn100198 on 3/18/21.
 */
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
