package net.yuuzu.data.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProjectRequest(
    val beforeRepairPhotos: List<String>,
    val afterRepairPhotos: List<String>,
    val attachments: List<String>,
    val handlingMethod: String,
    val signature: String,
)