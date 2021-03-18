package com.vtnd.duynn.presentation.ui.auth.login

import com.vtnd.duynn.data.error.AppError
import com.vtnd.duynn.presentation.base.MVIIntent
import com.vtnd.duynn.presentation.base.MVIPartialStateChange
import com.vtnd.duynn.presentation.base.MVISingleEvent
import com.vtnd.duynn.presentation.base.MVIViewState
import com.vtnd.duynn.utils.types.ValidateErrorType.ValidationError

/**
 * Created by duynn100198 on 3/18/21.
 */
interface LoginContract {
    data class ViewState(
        val errors: Set<ValidationError>,
        val isLoading: Boolean,
        //
        val emailChanged: Boolean,
        val passwordChanged: Boolean,
        //
        val email: String?,
        val password: String?,
    ) : MVIViewState {
        companion object {
            fun initial(
                email: String?,
                password: String?,
            ) = ViewState(
                errors = emptySet(),
                isLoading = false,
                emailChanged = false,
                passwordChanged = false,
                email = email,
                password = password,
            )
        }
    }

    sealed class ViewIntent : MVIIntent {
        data class EmailChanged(val email: String?) : ViewIntent()
        data class PasswordChanged(val password: String?) : ViewIntent()

        object Submit : ViewIntent()

        object EmailChangedFirstTime : ViewIntent()
        object PasswordChangedFirstTime : ViewIntent()
    }

    sealed class PartialStateChange : MVIPartialStateChange<ViewState> {
        data class ErrorsChanged(val errors: Set<ValidationError>) : PartialStateChange() {
            override fun reduce(viewState: ViewState) = viewState.copy(errors = errors)
        }

        sealed class Login : PartialStateChange() {
            object Loading : Login()
            object LoginSuccess : Login()
            data class LoginFailure(val throwable: AppError) : Login()

            override fun reduce(viewState: ViewState): ViewState {
                return when (this) {
                    Loading -> viewState.copy(isLoading = true)
                    is LoginSuccess -> viewState.copy(isLoading = false)
                    is LoginFailure -> viewState.copy(isLoading = false)
                }
            }
        }

        sealed class FirstChange : PartialStateChange() {
            object EmailChangedFirstTime : FirstChange()
            object PasswordChangedFirstTime : FirstChange()

            override fun reduce(viewState: ViewState): ViewState {
                return when (this) {
                    EmailChangedFirstTime -> viewState.copy(emailChanged = true)
                    PasswordChangedFirstTime -> viewState.copy(passwordChanged = true)
                }
            }
        }

        sealed class FormValueChange : PartialStateChange() {
            override fun reduce(viewState: ViewState): ViewState {
                return when (this) {
                    is EmailChanged -> viewState.copy(email = email)
                    is PasswordChanged -> viewState.copy(password = password)
                }
            }

            data class EmailChanged(val email: String?) : FormValueChange()
            data class PasswordChanged(val password: String?) : FormValueChange()
        }
    }

    sealed class SingleEvent : MVISingleEvent {
        object LoginSuccess : SingleEvent()
        data class LoginFailure(val throwable: AppError) : SingleEvent()
    }
}
