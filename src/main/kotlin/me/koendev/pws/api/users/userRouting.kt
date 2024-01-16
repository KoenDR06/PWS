package me.koendev.pws.api.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.database.User
import me.koendev.pws.database.UserItem
import me.koendev.pws.plugins.userService
import org.jetbrains.exposed.sql.transactions.transaction
import println

fun Routing.userRouting() {
    get("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = transaction { UserItem.findById(id) }
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("/api/users") {
        val user = call.receive<User>()
        val id = UserItem.new {
            user.username
        }
        call.respond(HttpStatusCode.OK, id)
    }

    delete("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            userService.delete(id)
        } catch(e: Exception) {
            e.println()
        }
    }

    put("/api/users/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            val user = call.receive<User>()
            //todo: fix this
            //userService.update(id, user = user)
            call.respond(HttpStatusCode.OK, id)
        } catch(e: Exception) {
            e.println()
        }
    }
}