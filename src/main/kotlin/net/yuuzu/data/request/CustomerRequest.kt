package net.yuuzu.data.request

import kotlinx.serialization.Serializable

@Serializable
data class CustomerRequest(
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)