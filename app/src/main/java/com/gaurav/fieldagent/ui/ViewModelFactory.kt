package com.gaurav.fieldagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaurav.fieldagent.domain.usecase.GetUsersUseCase
import com.gaurav.fieldagent.domain.usecase.SearchUsersUseCase
import com.gaurav.fieldagent.utils.NetworkStatus

class ViewModelFactory(
    private val getUsersUseCase: GetUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val networkStatus: NetworkStatus
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(getUsersUseCase, searchUsersUseCase, networkStatus) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}