package net.yuuzu.data.model.customer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Customer(
    @SerialName("_id") val id: String = ObjectId.get().toString(),
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
)
