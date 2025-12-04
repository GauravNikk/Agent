package com.gaurav.fieldagent.data.repository

import androidx.paging.PagingData
import com.gaurav.fieldagent.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUsers(): Flow<PagingData<User>>

    fun searchUsers(query: String): Flow<PagingData<User>>
}