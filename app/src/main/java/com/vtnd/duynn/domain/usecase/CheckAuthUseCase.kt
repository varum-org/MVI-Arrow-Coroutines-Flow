package com.vtnd.duynn.domain.usecase

import com.vtnd.duynn.domain.repository.UserRepository

/**
 * Created by duynn100198 on 3/19/21.
 */
class CheckAuthUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.checkAuth()
}
