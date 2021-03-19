package com.vtnd.duynn.presentation.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtnd.duynn.domain.usecase.CheckAuthUseCase
import com.vtnd.duynn.utils.extension.Event
import com.vtnd.duynn.utils.extension.asLiveData
import kotlinx.coroutines.launch

/**
 * Created by duynn100198 on 3/19/21.
 */
class SplashViewModel(private val checkAuthUseCase: CheckAuthUseCase) : ViewModel() {
    private val _authEvent = MutableLiveData<Event<Boolean>>()
    val authEvent get() = _authEvent.asLiveData()

    init {
        viewModelScope.launch {
            val auth = checkAuthUseCase.invoke().fold(ifLeft = { false }, ifRight = { it })
            _authEvent.value = Event(auth)
        }
    }
}
