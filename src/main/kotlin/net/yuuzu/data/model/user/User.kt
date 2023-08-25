package net.yuuzu.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class User(
    val username: String,
    val password: String,
    val salt: String,
    @SerialName("_id") val id: String = ObjectId.get().toString()
)

