package com.gaurav.fieldagent.domain.usecase

import androidx.paging.PagingData
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(private val userRepository: UserRepository) {

    fun searchLocalUsers(query: String): Flow<PagingData<User>> {
        return userRepository.searchLocalUsers(query)
    }

    fun searchRemoteUsers(query: String): Flow<PagingData<User>> {
        return userRepository.searchRemoteUsers(query)
    }
}