package net.yuuzu.data.model.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserById(id: String): User? {
        return users.findOne(User::id eq id)
    }

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(User::username eq username)
    }

    override suspend fun insertUser(user: User): Boolean {
        if (getUserByUsername(user.username) != null) return false
        return users.insertOne(user).wasAcknowledged()
    }
}