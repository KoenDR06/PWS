package me.koendev.pws

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.koendev.pws.plugins.*

val dotEnv = dotenv()
var currentUserId = -1

fun main() {
    embeddedServer(Netty, port = dotEnv["HOSTING_PORT"].toInt(), host = dotEnv["HOSTING_IP"], module = Application::module) // RUN IN ROOT!!!
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
