package com.vtnd.duynn.di

import com.vtnd.duynn.presentation.mapper.UserMapper
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
val mapperModule = module {
    factory { UserMapper() }
}
