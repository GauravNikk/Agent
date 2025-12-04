package com.gaurav.fieldagent.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gaurav.fieldagent.data.model.User
import com.gaurav.fieldagent.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException

class UserSearchPagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val position = params.key ?: 0
        val limit = params.loadSize
        return try {
            val response = apiService.searchUsers(query, limit, position * limit)
            val users = response.users
            LoadResult.Page(
                data = users,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (users.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
