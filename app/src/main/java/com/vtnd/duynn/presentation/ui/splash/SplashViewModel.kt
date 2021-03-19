package com.vtnd.duynn.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtnd.duynn.domain.usecase.CheckAuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by duynn100198 on 3/19/21.
 */
class SplashViewModel(private val checkAuthUseCase: CheckAuthUseCase) : ViewModel() {
    private val _authEvent = MutableStateFlow(false)
    val authEvent get() = _authEvent.asStateFlow()

    init {
        viewModelScope.launch {
            val auth = checkAuthUseCase.invoke().fold(ifLeft = { false }, ifRight = { it })
            _authEvent.value = auth
        }
    }
}
