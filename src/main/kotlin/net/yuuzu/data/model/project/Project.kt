package net.yuuzu.data.model.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Project(
    @SerialName("_id") @BsonId val id: String = ObjectId().toString(),
    val name: String,
    val category: String,
    val description: String,
    val customerName: String,
    val location: String,
    val status: Int, // 0: not started, 1: in progress, 2: Done, 3: Closed 99: cancelled
    val cost: Int,
    val deviceName: String,
    val faultReason: String,
    val beforeRepairPhotos: List<String>, // 存儲圖片URL或ID的列表
    val afterRepairPhotos: List<String>, // 存儲圖片URL或ID的列表
    val attachments: List<String>, // 存儲附件URL或ID的列表
    val signature: String, // 存儲SVG字串格式的簽名
    val handlingMethod: String
)
