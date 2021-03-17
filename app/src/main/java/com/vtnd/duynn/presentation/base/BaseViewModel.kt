package com.vtnd.duynn.presentation.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
open class BaseViewModel : ViewModel() {
    init {
        @Suppress("LeakingThis")
        Timber.d("$this::init")
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        Timber.d("$this::onCleared")
    }
}
