package com.vtnd.duynn.utils.types

import androidx.core.util.PatternsCompat

/**
 * Created by duynn100198 on 3/17/21.
 */
object ValidateErrorType {
    enum class ValidationError {
        INVALID_EMAIL_ADDRESS,
        INVALID_PASSWORD,
    }

    fun validateEmail(email: String?): Set<ValidationError> {
        val errors = mutableSetOf<ValidationError>()

        if (email == null || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            errors += ValidationError.INVALID_EMAIL_ADDRESS
        }

        // more validation here

        return errors
    }

    fun validatePassword(password: String?): Set<ValidationError> {
        val errors = mutableSetOf<ValidationError>()

        if (password == null || password != "123456") {
            errors += ValidationError.INVALID_PASSWORD
        }

        // more validation here

        return errors
    }
}
