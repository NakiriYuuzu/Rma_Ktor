package net.yuuzu.data.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Schedule(
    @SerialName("_id") val id: String = ObjectId.get().toString(),
    val userId: String,
    val projectId: String,
    val scheduleTime: String, // DateTime 2022-02-02 02:02:02
)