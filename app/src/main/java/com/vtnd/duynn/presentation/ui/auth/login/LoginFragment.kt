package com.vtnd.duynn.presentation.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.vtnd.duynn.R
import com.vtnd.duynn.data.error.getMessage
import com.vtnd.duynn.databinding.FragmentLoginBinding
import com.vtnd.duynn.presentation.base.BaseFragment
import com.vtnd.duynn.presentation.ui.auth.login.LoginContract.*
import com.vtnd.duynn.utils.extension.*
import com.vtnd.duynn.utils.types.ValidateErrorType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import timber.log.Timber

/**
 * Created by duynn100198 on 3/18/21.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class LoginFragment : BaseFragment<ViewIntent, ViewState, SingleEvent, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel by viewModel<LoginViewModel>(state = emptyState())
    override val viewBinding by viewBinding(FragmentLoginBinding::bind)

    override fun setUpView(view: View, savedInstanceState: Bundle?) = with(viewBinding) {
        val state = viewModel.viewState.value
        emailEditText.editText!!.setText(state.email)
        passwordEditText.editText!!.setText(state.password)
    }

    override fun handleSingleEvent(event: SingleEvent) {
        return when (event) {
            is SingleEvent.LoginSuccess -> {
                viewBinding.root.snack("Login Success").show()
            }
            is SingleEvent.LoginFailure -> {
                viewBinding.root.snack("Login Failure ${event.throwable.getMessage()}").show()
            }
        }
    }

    override fun render(viewState: ViewState) {
        Timber.d("ViewState=$viewState")
        val emailErrorMessage =
            if (ValidateErrorType.ValidationError.INVALID_EMAIL_ADDRESS in viewState.errors) {
                "Invalid email. Email is olololoe1001st2@gmail.com"
            } else null

        if (viewState.emailChanged && viewBinding.emailEditText.error != emailErrorMessage) {
            viewBinding.emailEditText.error = emailErrorMessage
        }

        val passwordErrorMessage =
            if (ValidateErrorType.ValidationError.INVALID_PASSWORD in viewState.errors) {
                "Invalid password. Password is 123456"
            } else null

        if (viewState.passwordChanged && viewBinding.passwordEditText.error != passwordErrorMessage) {
            viewBinding.passwordEditText.error = passwordErrorMessage
        }

        TransitionManager.beginDelayedTransition(
            viewBinding.root,
            AutoTransition()
                .addTarget(viewBinding.progressBar)
                .addTarget(viewBinding.addButton)
                .setDuration(200)
        )
        viewBinding.progressBar.isInvisible = !viewState.isLoading
        viewBinding.addButton.isInvisible = viewState.isLoading
    }

    override fun viewIntents(): Flow<ViewIntent> = viewBinding.run {
        merge(
            emailEditText
                .editText!!
                .textChanges()
                .map { ViewIntent.EmailChanged(it?.toString()) },
            passwordEditText
                .editText!!
                .textChanges()
                .map { ViewIntent.PasswordChanged(it?.toString()) },
            addButton
                .clicks()
                .map { ViewIntent.Submit },
            emailEditText
                .editText!!
                .firstChange()
                .map { ViewIntent.EmailChangedFirstTime },
            passwordEditText
                .editText!!
                .firstChange()
                .map { ViewIntent.PasswordChangedFirstTime },
        )
    }
}
