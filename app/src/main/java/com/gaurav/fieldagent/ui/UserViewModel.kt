package com.gaurav.fieldagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.domain.usecase.GetUsersUseCase
import com.gaurav.fieldagent.domain.usecase.SearchUsersUseCase
import com.gaurav.fieldagent.utils.NetworkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val networkStatus: NetworkStatus
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkStatus.isOnline

    fun getUsers(): Flow<PagingData<User>> {
        return getUsersUseCase().cachedIn(viewModelScope)
    }

    fun searchLocalUsers(query: String): Flow<PagingData<User>> {
        return searchUsersUseCase.searchLocalUsers(query).cachedIn(viewModelScope)
    }

    fun searchRemoteUsers(query: String): Flow<PagingData<User>> {
        return searchUsersUseCase.searchRemoteUsers(query).cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        networkStatus.unregisterCallback()
    }
}