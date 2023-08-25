package net.yuuzu.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.data.model.customer.CustomerDataSource
import net.yuuzu.data.model.image.ImageDataSource
import net.yuuzu.data.model.project.ProjectDataSource
import net.yuuzu.data.model.schedule.ScheduleDataSource
import net.yuuzu.data.model.user.UserDataSource
import net.yuuzu.route.*
import net.yuuzu.security.hashing.SHA256HashingService
import net.yuuzu.security.token.JwtTokenService
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    val tokenService = JwtTokenService()
    val hashingService = SHA256HashingService()

    val userDataSource: UserDataSource by inject(UserDataSource::class.java)
    val customerDataSource: CustomerDataSource by inject(CustomerDataSource::class.java)
    val projectDataSource: ProjectDataSource by inject(ProjectDataSource::class.java)
    val scheduleDataSource: ScheduleDataSource by inject(ScheduleDataSource::class.java)
    val imageDataSource: ImageDataSource by inject(ImageDataSource::class.java)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // authRoute
        signIn(userDataSource, hashingService, tokenService)
        signUp(hashingService, userDataSource)
        authenticate(userDataSource)

        // customerRoute
        customer(customerDataSource)

        // projectRoute
        project(projectDataSource)

        // scheduleRoute
        schedule(userDataSource, scheduleDataSource)

        // imageRoute
        image(imageDataSource)

        // jobRoute
        job(userDataSource, scheduleDataSource, projectDataSource)
    }
}
