package com.gaurav.fieldagent.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gaurav.fieldagent.data.UserRemoteMediator
import com.gaurav.fieldagent.data.UserSearchPagingSource
import com.gaurav.fieldagent.data.local.AppDatabase
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<User>> {
        val pagingSourceFactory = { appDatabase.userDao().getUsers() }
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(apiService, appDatabase),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun searchUsers(query: String): Flow<PagingData<User>> {
        return flow {
            val localUserCount = appDatabase.userDao().getUserCountForQuery("%${query}%")
            val pager = if (localUserCount > 0) {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { appDatabase.userDao().searchUsers("%${query}%") }
                )
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { UserSearchPagingSource(apiService, query) }
                )
            }
            emit(pager.flow)
        }.flatMapLatest { it }
    }
}