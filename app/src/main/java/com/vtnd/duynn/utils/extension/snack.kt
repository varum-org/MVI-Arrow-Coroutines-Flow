package com.vtnd.duynn.utils.extension

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

/**
 * Created by duynn100198 on 3/17/21.
 */
inline fun View.snack(
    message: String,
    length: SnackBarLength = SnackBarLength.SHORT,
    crossinline f: Snackbar.() -> Unit = {}
) = Snackbar.make(this, message, length.rawValue).apply {
    f()
    show()
}

enum class SnackBarLength(val rawValue: Int) {
    SHORT(Snackbar.LENGTH_SHORT),

    LONG(Snackbar.LENGTH_LONG),

    INDEFINITE(Snackbar.LENGTH_INDEFINITE);
}
