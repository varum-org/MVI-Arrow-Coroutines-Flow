package com.vtnd.duynn.domain.usecase

import com.vtnd.duynn.domain.repository.UserRepository

/**
 * Created by duynn100198 on 3/19/21.
 */
class UserObservableUseCase(private val userRepository: UserRepository) {
    operator fun invoke() = userRepository.userObservable()
}
