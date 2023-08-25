package net.yuuzu.data.model.image

interface ImageDataSource {
    suspend fun getAllImages(): List<Image>
    suspend fun getImageByImageId(imageId: String): Image?
    suspend fun insertImage(image: Image): Boolean
    suspend fun insertImages(imageList: List<Image>): Boolean
    suspend fun deleteImage(imageId: String): Boolean
}