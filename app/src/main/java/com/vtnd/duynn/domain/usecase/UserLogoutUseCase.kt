package com.vtnd.duynn.domain.usecase

import com.vtnd.duynn.domain.repository.UserRepository

/**
 * Created by duynn100198 on 3/21/21.
 */
class UserLogoutUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.logout()
}
