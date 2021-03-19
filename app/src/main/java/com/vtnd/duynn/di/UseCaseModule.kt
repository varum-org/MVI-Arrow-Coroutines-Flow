package com.vtnd.duynn.di

import com.vtnd.duynn.domain.usecase.CheckAuthUseCase
import com.vtnd.duynn.domain.usecase.UserLoginUseCase
import com.vtnd.duynn.domain.usecase.UserObservableUseCase
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
val useCaseModule = module {
    factory { UserLoginUseCase(userRepository = get()) }
    factory { CheckAuthUseCase(userRepository = get()) }
    factory { UserObservableUseCase(userRepository = get()) }
}
