package me.koendev.pws.api.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.database.User
import me.koendev.pws.database.UserService
import me.koendev.pws.plugins.database
import println

fun Routing.userRouting() {
    get("/api/users/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("/api/users/${userId}")
    }
    get("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = UserService(database = database).read(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("/api/users/") {
        call.respondRedirect("/api/users")
    }
    post("/api/users") {
        val user = call.receive<User>()
        val id = UserService(database = database).create(user)
        call.respond(HttpStatusCode.OK, id)
    }

    delete("/api/users/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("/api/users/${userId}")
    }
    delete("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            UserService(database = database).delete(id)
        } catch(e: Exception) {
            e.println()
        }
    }

    put("/api/users/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("/api/users/${userId}")
    }
    put("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            val user = call.receive<User>()
            UserService(database = database).update(id, user = user)
            call.respond(HttpStatusCode.OK, id)
        } catch(e: Exception) {
            e.println()
        }
    }
}