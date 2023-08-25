package net.yuuzu.data.request

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRequest(
    val userId: String,
    val projectId: String,
    val scheduleTime: String // epoch time
) {
    fun toJson() {
        println(
            """
            {
                "userId": "$userId",
                "projectId": "$projectId",
                "scheduleTime": $scheduleTime
            }
            """.trimIndent()
        )
    }
}
