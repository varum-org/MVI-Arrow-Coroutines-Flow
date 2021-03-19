package com.vtnd.duynn.utils.extension

import android.view.View

/**
 * Created by duynn100198 on 3/19/21.
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}
