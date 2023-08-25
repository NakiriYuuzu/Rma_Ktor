package net.yuuzu.data.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectRequest(
    val name: String,
    val category: String,
    val description: String,
    val customerName: String,
    val location: String,
    val cost: Int,
    val status: Int,
    val deviceName: String,
    val faultReason: String,
)