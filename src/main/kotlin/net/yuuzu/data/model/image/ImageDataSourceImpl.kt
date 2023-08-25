package net.yuuzu.data.model.image

import net.yuuzu.common.DatabaseFactory
import org.litote.kmongo.coroutine.CoroutineDatabase

class ImageDataSourceImpl(
    db: CoroutineDatabase
) : ImageDataSource {
    private val images = db.getCollection<Image>()
    private val client = DatabaseFactory.client
    override suspend fun getAllImages(): List<Image> {
        return images.find().toList()
    }

    override suspend fun getImageByImageId(imageId: String): Image? {
        return images.findOneById(imageId)
    }

    override suspend fun insertImage(image: Image): Boolean {
        return images.insertOne(image).wasAcknowledged()
    }

    override suspend fun insertImages(imageList: List<Image>): Boolean {
        val session = client.startSession()
        session.startTransaction()
        try {
            images.insertMany(imageList)
            session.commitTransaction()
            return true
        } catch (e: Exception) {
            session.abortTransaction()
        } finally {
            session.close()
        }
        return false
    }

    override suspend fun deleteImage(imageId: String): Boolean {
        return images.deleteOneById(imageId).wasAcknowledged()
    }
}