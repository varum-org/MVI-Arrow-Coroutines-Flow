package com.vtnd.duynn.di

import com.vtnd.duynn.data.scheduler.DefaultDispatcher
import com.vtnd.duynn.data.scheduler.IODispatcher
import com.vtnd.duynn.data.scheduler.MainDispatcher
import com.vtnd.duynn.data.scheduler.UnconfinedDispatcher
import com.vtnd.duynn.domain.scheduler.AppDispatchers.DEFAULT
import com.vtnd.duynn.domain.scheduler.AppDispatchers.IO
import com.vtnd.duynn.domain.scheduler.AppDispatchers.MAIN
import com.vtnd.duynn.domain.scheduler.AppDispatchers.UNCONFINED
import com.vtnd.duynn.domain.scheduler.DispatchersProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
val dispatcherModule = module {
    single<DispatchersProvider>(named(IO)) { IODispatcher() }
    single<DispatchersProvider>(named(DEFAULT)) { DefaultDispatcher() }
    single<DispatchersProvider>(named(MAIN)) { MainDispatcher() }
    single<DispatchersProvider>(named(UNCONFINED)) { UnconfinedDispatcher() }
}