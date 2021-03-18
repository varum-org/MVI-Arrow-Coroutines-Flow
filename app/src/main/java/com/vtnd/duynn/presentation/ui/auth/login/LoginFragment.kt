package com.vtnd.duynn.presentation.ui.auth.login

import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.vtnd.duynn.R
import com.vtnd.duynn.data.error.getMessage
import com.vtnd.duynn.databinding.FragmentLoginBinding
import com.vtnd.duynn.presentation.base.BaseFragment
import com.vtnd.duynn.utils.extension.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import timber.log.Timber

/**
 * Created by duynn100198 on 3/18/21.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private val loginViewModel by viewModel<LoginViewModel>(state = emptyState())
    private val loginBinding by viewBinding(FragmentLoginBinding::bind)

    override fun setUpView() = with(loginBinding) {
        val state = loginViewModel.viewState.value
        emailEditText.editText!!.setText(state.email)
        passwordEditText.editText!!.setText(state.password)
    }

    override fun bindView() {
        // observe view model
        loginViewModel.viewState
            .onEach { render(it) }
            .launchWhenStartedUntilStopped(this@LoginFragment)

        // observe single event
        loginViewModel.singleEvent
            .onEach { handleSingleEvent(it) }
            .launchWhenStartedUntilStopped(this@LoginFragment)

        // pass view intent to view model
        intents()
            .onEach { loginViewModel.processIntent(it) }
            .launchIn(lifecycleScope)
    }

    private fun handleSingleEvent(event: SingleEvent) {
        return when (event) {
            is SingleEvent.LoginSuccess -> {
                loginBinding.root.snack("Login Success").show()
            }
            is SingleEvent.LoginFailure -> {
                loginBinding.root.snack("Login Failure ${event.throwable.getMessage()}").show()
            }
        }
    }

    private fun render(viewState: ViewState) {
        Timber.d("ViewState=$viewState")
        val emailErrorMessage = if (ValidationError.INVALID_EMAIL_ADDRESS in viewState.errors) {
            "Invalid email. Email is olololoe1001st2@gmail.com"
        } else {
            null
        }
        if (viewState.emailChanged && loginBinding.emailEditText.error != emailErrorMessage) {
            loginBinding.emailEditText.error = emailErrorMessage
        }

        val passwordErrorMessage = if (ValidationError.INVALID_PASSWORD in viewState.errors) {
            "Invalid password. Password is 123456"
        } else {
            null
        }
        if (viewState.passwordChanged && loginBinding.passwordEditText.error != passwordErrorMessage) {
            loginBinding.passwordEditText.error = passwordErrorMessage
        }

        TransitionManager.beginDelayedTransition(
            loginBinding.root,
            AutoTransition()
                .addTarget(loginBinding.progressBar)
                .addTarget(loginBinding.addButton)
                .setDuration(200)
        )
        loginBinding.progressBar.isInvisible = !viewState.isLoading
        loginBinding.addButton.isInvisible = viewState.isLoading
    }

    private fun intents(): Flow<ViewIntent> = loginBinding.run {
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
