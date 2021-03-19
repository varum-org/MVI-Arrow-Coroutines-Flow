package com.vtnd.duynn.di

import com.vtnd.duynn.presentation.ui.auth.login.LoginViewModel
import com.vtnd.duynn.presentation.ui.splash.SplashViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
/**
 * You can create your ViewModel with scope, however it is not required because
 * 1 ViewModel can be used by several LifeCycleOwners.
 */
@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel {
        LoginViewModel(
            userMapper = get(),
            loginUseCase = get(),
            savedStateHandle = it.get()
        )
    }
    viewModel {
        SplashViewModel(checkAuthUseCase = get())
    }
}
