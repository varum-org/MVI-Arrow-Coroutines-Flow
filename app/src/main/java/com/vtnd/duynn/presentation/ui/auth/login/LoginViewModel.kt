package com.vtnd.duynn.presentation.ui.auth.login

import androidx.core.util.PatternsCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.vtnd.duynn.data.repository.source.remote.body.LoginBody
import com.vtnd.duynn.domain.usecase.UserLoginUseCase
import com.vtnd.duynn.presentation.base.BaseViewModel
import com.vtnd.duynn.presentation.mapper.UserMapper
import com.vtnd.duynn.utils.extension.flatMapFirst
import com.vtnd.duynn.utils.extension.leftOrNull
import com.vtnd.duynn.utils.extension.rightOrNull
import com.vtnd.duynn.utils.extension.withLatestFrom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * Created by duynn100198 on 3/18/21.
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class LoginViewModel(
    private val userMapper: UserMapper,
    private val loginUseCase: UserLoginUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _eventChannel = Channel<SingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<ViewIntent>(extraBufferCapacity = 64)

    val viewState: StateFlow<ViewState>
    val singleEvent: Flow<SingleEvent> get() = _eventChannel.receiveAsFlow()

    suspend fun processIntent(intent: ViewIntent) = _intentFlow.emit(intent)

    init {
        val initialViewState = ViewState.initial(
            email = savedStateHandle.get<String?>("email"),
            password = savedStateHandle.get<String?>("password")
        )
        Timber.d("[ADD_VM] initialVS: $initialViewState")

        viewState = _intentFlow
            .toPartialStateChangesFlow()
            .sendSingleEvent()
            .scan(initialViewState) { state, change -> change.reduce(state) }
            .catch { Timber.d("[ADD_VM] Throwable: $it") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialViewState)

    }

    private fun Flow<PartialStateChange>.sendSingleEvent(): Flow<PartialStateChange> {
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
            _eventChannel.send(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialStateChangesFlow(): Flow<PartialStateChange> {
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
            }
                .shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed()
                )

        val loginChanges = filterIsInstance<ViewIntent.Submit>()
            .withLatestFrom(userFormFlow) { _, userForm -> userForm }
            .mapNotNull {
                it.rightOrNull()
            }
            .flatMapFirst { user ->
                flow {
                    loginUseCase.invoke(user.email!!, user.password!!).fold(
                        ifRight = {
                            @Suppress("USELESS_CAST")
                            emit(PartialStateChange.Login.LoginSuccess)
                        },
                        ifLeft = {
                            emit(
                                PartialStateChange.Login.LoginFailure(it)
                            )
                        }
                    )
                }
                    .onStart { emit(PartialStateChange.Login.Loading) }
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
                .map { PartialStateChange.FormValueChange.EmailChanged(it) },
        )
        return merge(
            userFormFlow
                .map {
                    PartialStateChange.ErrorsChanged(
                        it.leftOrNull()
                            ?: emptySet()
                    )
                },
            loginChanges,
            firstChanges,
            formValuesChanges,
        )
    }

    private companion object {

        fun validateEmail(email: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if (email == null || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                errors += ValidationError.INVALID_EMAIL_ADDRESS
            }

            // more validation here

            return errors
        }

        fun validatePassword(password: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if (password == null || password != "123456") {
                errors += ValidationError.INVALID_PASSWORD
            }

            // more validation here

            return errors
        }
    }
}
