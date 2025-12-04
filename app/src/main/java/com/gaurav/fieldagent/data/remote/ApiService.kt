package com.gaurav.fieldagent.data.remote

import com.gaurav.fieldagent.data.model.Post
import com.gaurav.fieldagent.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): UserResponse

    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): UserResponse

    @GET("posts/user/{id}")
    suspend fun getPostsForUser(
        @Path("id") userId: Int
    ): PostResponse
}

data class UserResponse(
    val users: List<User>
)

data class PostResponse(
    val posts: List<Post>
)
