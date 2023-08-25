package net.yuuzu.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.data.model.schedule.Schedule
import net.yuuzu.data.model.schedule.ScheduleDataSource
import net.yuuzu.data.model.user.UserDataSource
import net.yuuzu.data.request.ScheduleRequest

fun Route.schedule(
    userDataSource: UserDataSource,
    scheduleDataSource: ScheduleDataSource,
) {
    authenticate {
        get("/schedule") {
            val schedules = scheduleDataSource.getSchedules()
            call.respond(HttpStatusCode.OK, schedules)
        }

        get("/schedule/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing schedule ID")
                return@get
            }

            val schedule = scheduleDataSource.getSchedule(id)
            if (schedule == null) {
                call.respond(HttpStatusCode.NotFound, "Schedule not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, schedule)
        }

        get("/schedule/user") {
            val id = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                return@get
            }

            val user = userDataSource.getUserById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }

            val schedule = scheduleDataSource.getScheduleByUser(id)
            call.respond(HttpStatusCode.OK, schedule)
        }

        post("/schedule") {
            val request = call.receive<ScheduleRequest>()
            val areFillBlank = request.projectId.isBlank() || request.scheduleTime.isBlank()

            if (areFillBlank) {
                call.respond(HttpStatusCode.Conflict, "Schedule info is blank")
                return@post
            }

            val schedule = Schedule(
                userId = request.userId.ifBlank { "" },
                projectId = request.projectId,
                scheduleTime = request.scheduleTime,
            )

            val wasAcknowledged = scheduleDataSource.insertSchedule(schedule)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Schedule already exists")
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }

        put("/schedule/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@put
            }

            val updatedSchedule = call.receive<ScheduleRequest>()
            val currentSchedule = scheduleDataSource.getSchedule(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "Schedule not found")
                return@put
            }

            val scheduleTime = updatedSchedule.scheduleTime.ifBlank { currentSchedule.scheduleTime }

            val schedule = Schedule(
                userId = updatedSchedule.userId,
                projectId = updatedSchedule.projectId.ifBlank { currentSchedule.projectId },
                scheduleTime = scheduleTime
            )

            val wasAcknowledged = scheduleDataSource.updateSchedule(schedule)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Schedule something problem.")
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}