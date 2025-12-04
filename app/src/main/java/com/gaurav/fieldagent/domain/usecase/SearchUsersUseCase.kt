package com.gaurav.fieldagent.domain.usecase

import androidx.paging.PagingData
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(private val userRepository: UserRepository) {

    operator fun invoke(query: String): Flow<PagingData<User>> {
        return userRepository.searchUsers(query)
    }
}