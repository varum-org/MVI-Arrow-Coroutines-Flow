package com.vtnd.duynn.presentation.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * Created by duynn100198 on 3/17/21.
 */
abstract class BaseViewModel<
        I : MVIIntent,
        S : MVIViewState,
        E : MVISingleEvent,
        P : MVIPartialStateChange<S>>(
    private val initialViewState: S
) : ViewModel(), MVIViewModel<I, S, E> {
    /**
     * ViewState
     */
    private val _intentFlow = MutableSharedFlow<I>(extraBufferCapacity = 64)

    @ExperimentalCoroutinesApi
    override val viewState: StateFlow<S> by lazy(LazyThreadSafetyMode.NONE) {
        _intentFlow
            .toPartialStateChangesFlow()
            .sendSingleEvent()
            .scan(initialViewState) { state, change -> change.reduce(state) }
            .catch { Timber.d("[ADD_VM] Throwable: $it") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialViewState)
    }

    /**
     * Single event
     * Like: snackbar message, navigation event or a dialog trigger
     */
    val eventChannel = Channel<E>(Channel.BUFFERED)
    override val singleEvent: Flow<E> get() = eventChannel.receiveAsFlow()

    override suspend fun processIntent(intent: I) = _intentFlow.emit(intent)

    init {
        @Suppress("LeakingThis")
        Timber.d("$this::init")
    }

    abstract fun Flow<I>.toPartialStateChangesFlow(): Flow<P>

    abstract fun Flow<P>.sendSingleEvent(): Flow<P>

    @CallSuper
    override fun onCleared() {
        Timber.d("$this::onCleared")
    }
}
