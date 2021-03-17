package com.vtnd.duynn.utils.extension

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Created by duynn100198 on 3/17/21.
 */
fun <T : ViewBinding> AppCompatActivity.viewBinding(factory: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) { factory(layoutInflater) }
