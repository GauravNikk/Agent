package com.gaurav.fieldagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.domain.usecase.GetUsersUseCase
import com.gaurav.fieldagent.domain.usecase.SearchUsersUseCase
import kotlinx.coroutines.flow.Flow

class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    fun getUsers(): Flow<PagingData<User>> {
        return getUsersUseCase().cachedIn(viewModelScope)
    }

    fun searchUsers(query: String): Flow<PagingData<User>> {
        return searchUsersUseCase(query).cachedIn(viewModelScope)
    }
}