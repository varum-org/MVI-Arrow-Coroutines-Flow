package com.vtnd.duynn.presentation.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vtnd.duynn.data.repository.source.remote.body.LoginBody
import com.vtnd.duynn.domain.usecase.UserLoginUseCase
import com.vtnd.duynn.presentation.base.BaseViewModel
import com.vtnd.duynn.presentation.mapper.UserMapper
import com.vtnd.duynn.presentation.ui.auth.login.LoginContract.*
import com.vtnd.duynn.utils.extension.*
import com.vtnd.duynn.utils.types.ValidateErrorType.validateEmail
import com.vtnd.duynn.utils.types.ValidateErrorType.validatePassword
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * Created by duynn100198 on 3/18/21.
 */
@FlowPreview
@ExperimentalCoroutinesApi
class LoginViewModel(
    private val userMapper: UserMapper,
    private val loginUseCase: UserLoginUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ViewIntent, ViewState, SingleEvent, PartialStateChange>(
    ViewState.initial(
        email = savedStateHandle.get<String?>("email"),
        password = savedStateHandle.get<String?>("password")
    )
) {

    override fun Flow<PartialStateChange>.sendSingleEvent(): Flow<PartialStateChange> {
        return onEach { change ->
            val event = when (change) {
                is PartialStateChange.ErrorsChanged -> return@onEach
                PartialStateChange.Login.Loading -> return@onEach
                is PartialStateChange.Login.LoginSuccess -> SingleEvent.LoginSuccess
                is PartialStateChange.Login.LoginFailure -> SingleEvent.LoginFailure(change.throwable)
                PartialStateChange.FirstChange.EmailChangedFirstTime -> return@onEach
                PartialStateChange.FirstChange.PasswordChangedFirstTime -> return@onEach
                is PartialStateChange.FormValueChange.EmailChanged -> return@onEach
                is PartialStateChange.FormValueChange.PasswordChanged -> return@onEach
            }
            eventChannel.send(event)
        }
    }

    override fun Flow<ViewIntent>.toPartialStateChangesFlow(): Flow<PartialStateChange> {
        val emailErrors = filterIsInstance<ViewIntent.EmailChanged>()
            .map { it.email }
            .map { validateEmail(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )

        val passwordErrors = filterIsInstance<ViewIntent.PasswordChanged>()
            .map { it.password }
            .map { validatePassword(it) to it }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )

        val userFormFlow =
            combine(emailErrors, passwordErrors) { email, password ->
                val errors = email.first + password.first
                if (errors.isEmpty()) Either.Right(
                    LoginBody(email = email.second, password = password.second)
                ) else Either.Left(errors)
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed()
            )

        val loginChanges = filterIsInstance<ViewIntent.Submit>()
            .withLatestFrom(userFormFlow) { _, userForm -> userForm }
            .mapNotNull { it.rightOrNull() }
            .flatMapFirst { user ->
                flow {
                    loginUseCase.invoke(user.email!!, user.password!!).fold(
                        ifRight = { emit(PartialStateChange.Login.LoginSuccess) },
                        ifLeft = { emit(PartialStateChange.Login.LoginFailure(it)) }
                    )
                }.onStart { emit(PartialStateChange.Login.Loading) }
            }

        val firstChanges = merge(
            filterIsInstance<ViewIntent.EmailChangedFirstTime>()
                .map { PartialStateChange.FirstChange.EmailChangedFirstTime },
            filterIsInstance<ViewIntent.PasswordChangedFirstTime>()
                .map { PartialStateChange.FirstChange.PasswordChangedFirstTime }
        )

        val formValuesChanges = merge(
            emailErrors
                .map { it.second }
                .onEach { savedStateHandle.set("email", it) }
                .map { PartialStateChange.FormValueChange.EmailChanged(it) },
            passwordErrors
                .map { it.second }
                .onEach { savedStateHandle.set("password", it) }
                .map { PartialStateChange.FormValueChange.PasswordChanged(it) },
        )
        return merge(
            userFormFlow
                .map {
                    PartialStateChange.ErrorsChanged(it.leftOrNull() ?: emptySet())
                },
            loginChanges,
            firstChanges,
            formValuesChanges,
        )
    }
}
