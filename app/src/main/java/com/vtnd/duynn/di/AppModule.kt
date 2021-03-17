package com.vtnd.duynn.di

import com.vtnd.duynn.BuildConfig
import com.vtnd.duynn.utils.constants.Constants
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by duynn100198 on 3/17/21.
 */
val appModule = module {
    single(named(Constants.KEY_BASE_URL)) {
        BuildConfig.BASE_URL
    }
}
