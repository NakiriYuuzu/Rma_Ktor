package net.yuuzu.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.data.model.project.ProjectDataSource
import net.yuuzu.data.model.schedule.ScheduleDataSource
import net.yuuzu.data.model.user.UserDataSource
import net.yuuzu.data.response.JobResponse
import kotlinx.datetime.*

fun Route.job(
    userDataSource: UserDataSource,
    scheduleDataSource: ScheduleDataSource,
    projectDataSource: ProjectDataSource
) {
    authenticate {
        get("/job") {
            val data: MutableMap<String, ArrayList<JobResponse>> = mutableMapOf(
                "today" to arrayListOf(),
                "thisWeek" to arrayListOf(),
                "nextWeek" to arrayListOf()
            )

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

            val schedules = scheduleDataSource.getScheduleByUser(id)
            if (schedules.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "Schedule not found")
                return@get
            }

            val timeZone = TimeZone.currentSystemDefault()
            val now = Clock.System.now().toLocalDateTime(timeZone).date
            val firstDayOfWeekDate = now.minus(now.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
            val lastDayOfWeekDate = firstDayOfWeekDate.plus(6, DateTimeUnit.DAY)
            val firstDayOfNextWeekDate = lastDayOfWeekDate.plus(1, DateTimeUnit.DAY)
            val lastDayOfNextWeekDate = firstDayOfNextWeekDate.plus(6, DateTimeUnit.DAY)

            schedules.forEach { schedule ->
                val project = projectDataSource.getProject(schedule.projectId)
                if (project == null) {
                    call.respond(HttpStatusCode.NotFound, "Project not found")
                    return@get
                }
                val job = JobResponse(
                    projectId = project.id,
                    projectName = project.name,
                    projectCategory = project.category,
                    projectDescription = project.description,
                    customerName = project.customerName,
                    location = project.location,
                    status = project.status,
                    cost = project.cost,
                    deviceName = project.deviceName,
                    faultReason = project.faultReason,
                    beforeRepairPhotos = project.beforeRepairPhotos,
                    afterRepairPhotos = project.afterRepairPhotos,
                    attachments = project.attachments,
                    handlingMethod = project.handlingMethod,
                    signature = project.signature,
                    scheduleTime = schedule.scheduleTime
                )

                // epoch time to LocalDate
                val toDateTime = schedule.scheduleTime.substring(0, 10).split("-")
                val scheduleTime = LocalDate(toDateTime[0].toInt(), toDateTime[1].toInt(), toDateTime[2].toInt())

                when (scheduleTime) {
                    now -> data["today"]?.add(job)
                    // 如果需要顯示之前的天數，可以把now改成firstDayOfWeekDate
                    in now..lastDayOfWeekDate -> data["thisWeek"]?.add(job)
                    in firstDayOfNextWeekDate..lastDayOfNextWeekDate -> data["nextWeek"]?.add(job)
                }
            }

            data["today"]?.forEach {
                data["thisWeek"]?.add(it)
            }

            call.respond(HttpStatusCode.OK, data)
        }

        get("/job/{date}") {
            val data = arrayListOf<JobResponse>()

            val date = call.parameters["date"]
            if (date == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing date")
                return@get
            }

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

            val schedules = scheduleDataSource.getScheduleByUser(id)
            if (schedules.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "Schedule not found")
                return@get
            }

            val targetSchedule = schedules.filter {
                val targetDate = date.split("-")
                it.scheduleTime.substring(0, 10) == LocalDate(targetDate[0].toInt(), targetDate[1].toInt(), targetDate[2].toInt()).toString()
            }

            targetSchedule.forEach { schedule ->
                val project = projectDataSource.getProject(schedule.projectId)
                if (project == null) {
                    call.respond(HttpStatusCode.NotFound, "Project not found")
                    return@get
                }
                val job = JobResponse(
                    projectId = project.id,
                    projectName = project.name,
                    projectCategory = project.category,
                    projectDescription = project.description,
                    customerName = project.customerName,
                    location = project.location,
                    status = project.status,
                    cost = project.cost,
                    deviceName = project.deviceName,
                    faultReason = project.faultReason,
                    beforeRepairPhotos = project.beforeRepairPhotos,
                    afterRepairPhotos = project.afterRepairPhotos,
                    attachments = project.attachments,
                    handlingMethod = project.handlingMethod,
                    signature = project.signature,
                    scheduleTime = schedule.scheduleTime
                )

                data.add(job)
            }


            call.respond(HttpStatusCode.OK, data)
        }
    }
}