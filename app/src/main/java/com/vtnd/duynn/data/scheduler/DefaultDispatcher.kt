package com.vtnd.duynn.data.scheduler

import com.vtnd.duynn.domain.scheduler.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Created by duynn100198 on 3/17/21.
 */
class DefaultDispatcher : DispatchersProvider {
    override fun dispatcher(): CoroutineContext {
        return Dispatchers.Default
    }
}
