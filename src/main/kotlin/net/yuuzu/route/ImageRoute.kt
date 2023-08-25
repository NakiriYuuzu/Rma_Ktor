package net.yuuzu.route

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.common.save
import net.yuuzu.data.model.image.Image
import net.yuuzu.data.model.image.ImageDataSource

fun Route.image(
    imageDataSource: ImageDataSource
) {
    authenticate {
        get("/image") {
            val images = imageDataSource.getAllImages()
            call.respond(HttpStatusCode.OK, images)
        }
        get("/image/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@get
            }

            val image = imageDataSource.getImageByImageId(id)
            if (image != null) {
                call.respond(HttpStatusCode.OK, image)
            } else {
                call.respond(HttpStatusCode.NotFound, "Image not found")
            }
        }
        post("/image") {
            val multipart = call.receiveMultipart()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> Unit
                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            val contentType = part.contentType
                            if (contentType == ContentType.Image.JPEG || contentType == ContentType.Image.PNG) {
                                val path = "src/main/resources/static/images/"
                                val randomUniqueName = "${System.currentTimeMillis()}_${part.originalFileName}"
                                println(randomUniqueName)
                                part.save(path, randomUniqueName)

                                val image = Image(
                                    path = path + randomUniqueName,
                                )

                                imageDataSource.insertImage(image)
                            }
                        }
                    }
                    else -> Unit
                }
            }

            call.respond(HttpStatusCode.OK)
        }
        delete("/image/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@delete
            }

            if (imageDataSource.getImageByImageId(id) == null) {
                call.respond(HttpStatusCode.NotFound, "Image not found")
                return@delete
            }
            imageDataSource.deleteImage(id)
        }
    }
}