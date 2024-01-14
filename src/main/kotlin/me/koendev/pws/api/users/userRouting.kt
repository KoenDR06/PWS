package me.koendev.pws.api.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.database.User
import me.koendev.pws.database.UserService
import me.koendev.pws.plugins.database

fun Routing.userRouting() {
    get("/recepten/{user_id}/") {
        val userId = call.parameters["user_id"]
        call.respondRedirect("/recepten/${userId}")
    }
    get("/api/users/{user_id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = UserService(database = database).read(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("/api/users/") {call.respondRedirect("/api/users")}
    post("/api/users") {
        val user = call.receive<User>()
        val id = UserService(database = database).create(user)
        call.respond(HttpStatusCode.Created, id)
    }
}