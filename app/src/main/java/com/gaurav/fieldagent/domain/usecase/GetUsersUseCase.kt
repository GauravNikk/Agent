package com.gaurav.fieldagent.domain.usecase

import androidx.paging.PagingData
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val userRepository: UserRepository) {

    operator fun invoke(): Flow<PagingData<User>> {
        return userRepository.getUsers()
    }
}