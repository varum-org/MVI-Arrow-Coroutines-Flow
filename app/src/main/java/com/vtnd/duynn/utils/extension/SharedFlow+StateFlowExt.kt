package com.vtnd.duynn.utils.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by duynn100198 on 3/19/21.
 */

inline fun <T : Any> StateFlow<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit,
) = onEach { observer(it) }
    .launchIn(owner.lifecycleScope)

inline fun <T : Any> SharedFlow<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit,
) = onEach { observer(it) }
    .launchIn(owner.lifecycleScope)

