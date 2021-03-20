package com.vtnd.duynn.domain

import com.vtnd.duynn.data.error.AppError
import com.vtnd.duynn.utils.extension.Either

/**
 * Created by duynn100198 on 3/20/21.
 */
typealias DomainResult<T> = Either<AppError, T>
