package net.yuuzu.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.data.model.project.Project
import net.yuuzu.data.model.project.ProjectDataSource
import net.yuuzu.data.request.CreateProjectRequest
import net.yuuzu.data.request.UpdateProjectRequest

fun Route.project(
    projectDataSource: ProjectDataSource
) {
    authenticate {
        get("/project") {
            val projects = projectDataSource.getProjects()
            call.respond(HttpStatusCode.OK, projects)
        }

        get("/project/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@get
            }

            val project = projectDataSource.getProject(id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, project)
        }

        post("/project") {
            val request = call.receive<CreateProjectRequest>()
            val areFillBlank = request.name.isBlank() ||
                    request.category.isBlank() ||
                    request.description.isBlank() ||
                    request.customerName.isBlank() ||
                    request.location.isBlank() ||
                    request.deviceName.isBlank() ||
                    request.faultReason.isBlank()

            if (areFillBlank) {
                call.respond(HttpStatusCode.BadRequest, "Missing project information")
                return@post
            }

            val project = Project(
                name = request.name,
                category = request.category,
                description = request.description,
                customerName = request.customerName,
                location = request.location,
                status = 0,
                cost = request.cost,
                deviceName = request.deviceName,
                faultReason = request.faultReason,
                beforeRepairPhotos = emptyList(),
                afterRepairPhotos = emptyList(),
                attachments = emptyList(),
                handlingMethod = "",
                signature = "",
            )

            val wasAcknowledged = projectDataSource.insertProject(project)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Project already exists")
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }

        put("/project/edit/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@put
            }

            val editProject = call.receive<CreateProjectRequest>()
            val currentProject = projectDataSource.getProject(id)

            if (currentProject == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@put
            }

            val project = Project(
                name = editProject.name.ifBlank { currentProject.name },
                category = editProject.category.ifBlank { currentProject.category },
                description = editProject.description.ifBlank { currentProject.description },
                customerName = editProject.customerName.ifBlank { currentProject.customerName },
                location = editProject.location.ifBlank { currentProject.location },
                status = editProject.status,
                cost = editProject.cost,
                deviceName = editProject.deviceName.ifBlank { currentProject.deviceName },
                faultReason = editProject.faultReason.ifBlank { currentProject.faultReason },
                beforeRepairPhotos = emptyList(),
                afterRepairPhotos = emptyList(),
                attachments = emptyList(),
                handlingMethod = "",
                signature = "",
            )

            val wasAcknowledged = projectDataSource.updateProject(project)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Something went wrong")
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }

        put("project/update/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@put
            }

            var status = 1
            val updateProject = call.receive<UpdateProjectRequest>()
            val currentProject = projectDataSource.getProject(id)

            if (currentProject == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@put
            }

            if (currentProject.status == 99 || currentProject.status == 3) {
                call.respond(HttpStatusCode.Conflict, "Project is already finished")
                return@put
            }

            if (currentProject.status == 1) status = 2

            val project = Project(
                name = currentProject.name,
                category = currentProject.category,
                description = currentProject.description,
                customerName = currentProject.customerName,
                location = currentProject.location,
                status = status,
                cost = currentProject.cost,
                deviceName = currentProject.deviceName,
                faultReason = currentProject.faultReason,
                beforeRepairPhotos = updateProject.beforeRepairPhotos.ifEmpty { currentProject.beforeRepairPhotos },
                afterRepairPhotos = updateProject.afterRepairPhotos.ifEmpty { currentProject.afterRepairPhotos },
                attachments = updateProject.attachments.ifEmpty { currentProject.attachments },
                handlingMethod = updateProject.handlingMethod.ifBlank { currentProject.handlingMethod },
                signature = updateProject.signature.ifBlank { currentProject.signature },
            )

            val wasAcknowledged = projectDataSource.updateProject(project)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Something went wrong")
                return@put
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}