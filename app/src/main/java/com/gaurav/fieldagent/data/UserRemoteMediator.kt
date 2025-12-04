package com.gaurav.fieldagent.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gaurav.fieldagent.data.local.AppDatabase
import com.gaurav.fieldagent.data.local.RemoteKeys
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, User>() {

    private val userDao = appDatabase.userDao()
    private val remoteKeysDao = appDatabase.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    if (remoteKeys?.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKeys.nextKey
                }
            }

            val response = apiService.getUsers(
                limit = state.config.pageSize,
                skip = loadKey ?: 0
            )
            val users = response.users
            val endOfPaginationReached = users.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.clearUsers()
                    remoteKeysDao.clearRemoteKeys()
                }
                val prevKey = if (loadKey != null && loadKey > 0) loadKey - state.config.pageSize else null
                val nextKey = if (endOfPaginationReached) null else (loadKey ?: 0) + state.config.pageSize
                val keys = users.map {
                    RemoteKeys(userId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                userDao.insertAll(users)
                remoteKeysDao.insertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, User>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            remoteKeysDao.remoteKeysUserId(it.id)
        }
    }
}