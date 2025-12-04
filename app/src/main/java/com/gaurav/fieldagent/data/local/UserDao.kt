package com.gaurav.fieldagent.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaurav.fieldagent.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users")
    fun getUsers(): PagingSource<Int, User>

    @Query("SELECT * FROM users WHERE firstName LIKE :query OR lastName LIKE :query")
    fun searchUsers(query: String): PagingSource<Int, User>

    @Query("SELECT COUNT(*) FROM users WHERE firstName LIKE :query OR lastName LIKE :query")
    fun getUserCountForQuery(query: String): Flow<Int>

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}