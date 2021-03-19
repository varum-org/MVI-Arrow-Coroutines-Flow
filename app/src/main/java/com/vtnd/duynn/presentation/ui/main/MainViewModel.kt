package com.vtnd.duynn.presentation.ui.main

import androidx.lifecycle.ViewModel
import arrow.core.extensions.option.monad.flatten
import com.vtnd.duynn.domain.usecase.UserObservableUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Created by duynn100198 on 3/19/21.
 */
class MainViewModel(private val userObservableUseCase: UserObservableUseCase) : ViewModel() {

    val logoutEvent = userObservableUseCase.invoke()
        .map { it.toOption().flatten() }
        .distinctUntilChanged()
        .filter { it.isEmpty() }
        .map { }
}
