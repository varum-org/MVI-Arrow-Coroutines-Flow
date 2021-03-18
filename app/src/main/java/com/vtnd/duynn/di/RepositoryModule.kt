package com.vtnd.duynn.di

import com.vtnd.duynn.data.repository.UserRepositoryImpl
import com.vtnd.duynn.domain.repository.UserRepository
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
@OptIn(KoinApiExtension::class)
val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }
}
