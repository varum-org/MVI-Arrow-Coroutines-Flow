package com.vtnd.duynn.presentation.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtnd.duynn.domain.usecase.UserLogoutUseCase
import com.vtnd.duynn.utils.extension.fold
import kotlinx.coroutines.launch

/**
 * Created by duynn100198 on 3/21/21.
 */
class ProfileViewModel(private val userLogoutUseCase: UserLogoutUseCase) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userLogoutUseCase.invoke().fold(
                ifLeft = {
                    // something handle logout error
                },
                ifRight = {
                    // something handle logout success
                }
            )
        }
    }
}
