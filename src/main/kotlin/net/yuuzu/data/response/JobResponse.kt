package net.yuuzu.data.response

import kotlinx.serialization.Serializable

@Serializable
data class JobResponse(
    val projectId: String,
    val projectName: String,
    val projectCategory: String,
    val projectDescription: String,
    val customerName: String,
    val location: String,
    val status: Int,
    val cost: Int,
    val deviceName: String,
    val faultReason: String,
    val beforeRepairPhotos: List<String>,
    val afterRepairPhotos: List<String>,
    val attachments: List<String>,
    val handlingMethod: String,
    val signature: String,
    val scheduleTime: String,
)
