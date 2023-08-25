package net.yuuzu.data.model.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Image(
    @SerialName("_id") val id: String = ObjectId.get().toString(),
    val path: String
)
