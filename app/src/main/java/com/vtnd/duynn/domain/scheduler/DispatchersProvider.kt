package com.vtnd.duynn.domain.scheduler

import kotlin.coroutines.CoroutineContext

/**
 * Created by duynn100198 on 3/17/21.
 */
interface DispatchersProvider {
    fun dispatcher(): CoroutineContext
}
