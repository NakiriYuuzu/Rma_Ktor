package net.yuuzu.common

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object DatabaseFactory {
    private const val DB = "YuuzuMongo"
    private val mongoPassword = System.getenv("MONGO_PASSWORD")

    val client = KMongo.createClient(
        connectionString = "mongodb+srv://yuuzu:$mongoPassword@yuuzumongo.qgoorj3.mongodb.net/yuuzumongo?retryWrites=true&w=majority"
    ).coroutine

    fun createDatabase(): CoroutineDatabase {
        return client.getDatabase(DB)
    }
}