package com.vtnd.duynn.domain.usecase

import com.vtnd.duynn.domain.repository.UserRepository

/**
 * Created by duynn100198 on 3/17/21.
 */
class UserLoginUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        email: String,
        password: String
    ) = userRepository.login(email, password)
}
