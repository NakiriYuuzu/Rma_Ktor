package net.yuuzu.data.model.user

interface UserDataSource {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User): Boolean
}