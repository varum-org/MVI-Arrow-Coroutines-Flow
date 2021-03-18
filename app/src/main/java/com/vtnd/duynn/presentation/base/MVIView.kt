package com.vtnd.duynn.presentation.base

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

/**
 * Created by duynn100198 on 3/19/21.
 */
/**
 * Object representing a UI that will
 * a) emit its intents to a view model,
 * b) subscribes to a view model for rendering its UI.
 * c) subscribes to a view model for handling single UI event.
 *
 * @param I Top class of the [MVIIntent] that the [MVIView] will be emitting.
 * @param S Top class of the [MVIViewState] the [MVIView] will be subscribing to.
 * @param E Top class of the [MVISingleEvent] the [MVIView] will be subscribing to.
 */
interface MVIView<I : MVIIntent, S : MVIViewState, E : MVISingleEvent> {
    /**
     * Entry point for the [MVIView] to render itself based on a [MVIViewState].
     */
    fun render(viewState: S)

    /**
     * Entry point for the [MVIView] to handle single event.
     */
    fun handleSingleEvent(event: E)

    /**
     * Unique [Flow] used by the [MVIViewModel] to listen to the [MVIView].
     * All the [MVIView]'s [MVIIntent]s must go through this [Observable].
     */
    @CheckResult
    fun viewIntents(): Flow<I>
}
