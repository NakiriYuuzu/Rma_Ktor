package net.yuuzu.plugins

import io.ktor.server.application.*
import net.yuuzu.di.databaseModule
import net.yuuzu.di.sourceModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin){
        modules(
            databaseModule,
            sourceModule
        )
    }
}