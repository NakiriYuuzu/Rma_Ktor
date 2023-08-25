package net.yuuzu

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.yuuzu.plugins.*

// load my config file at resources/application.conf
val config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

fun main() {
    embeddedServer(Netty, port = config.port, host = config.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureSecurity()
    configureRouting()
//    configureSession()
}
