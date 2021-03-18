package com.vtnd.duynn.presentation.base

import androidx.annotation.CheckResult
import com.vtnd.duynn.presentation.ui.auth.login.LoginContract
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Created by duynn100198 on 3/19/21.
 */
/**
 * Object that will subscribes to a MVIView's [MVIIntent]s,
 * process it and emit a [MVIViewState] back.
 *
 * @param I Top class of the [MVIIntent] that the [MVIViewModel] will be subscribing to.
 * @param S Top class of the [MVIViewState] the [MVIViewModel] will be emitting.
 * @param E Top class of the [MVISingleEvent] that the [MVIViewModel] will be emitting.
 */
interface MVIViewModel<I : MVIIntent, S : MVIViewState, E : MVISingleEvent> {

    val viewState: StateFlow<S>
    val singleEvent: Flow<E>

    @CheckResult suspend fun processIntent(intent: I)
}
