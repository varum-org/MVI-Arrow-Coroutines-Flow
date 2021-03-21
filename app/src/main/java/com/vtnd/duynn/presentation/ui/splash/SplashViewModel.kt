package com.vtnd.duynn.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtnd.duynn.domain.usecase.CheckAuthUseCase
import com.vtnd.duynn.utils.extension.fold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Created by duynn100198 on 3/19/21.
 */
class SplashViewModel(private val checkAuthUseCase: CheckAuthUseCase) : ViewModel() {
    private val _authEvent = MutableSharedFlow<Boolean>()
    val authEvent get() = _authEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            val auth = checkAuthUseCase.invoke().fold({ false }) { it }
            _authEvent.emit(auth)
        }
    }
}
