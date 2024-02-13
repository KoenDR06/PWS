package me.koendev.pws.api.users

import LikeRequest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import me.koendev.pws.database.User
import me.koendev.pws.dotEnv
import me.koendev.pws.plugins.userService
import sha256
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit

fun Routing.userRouting() {
    post("/api/users/login") {
        val userToValidate = call.receive<User>()
        val user = userService.validate(userToValidate)

        if(user == null) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            val token = JWT.create()
                //.withAudience(dotEnv["JWT_AUDIENCE"])
                .withIssuer(dotEnv["JWT_ISSUER"])
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 60 * 30 * 1000))
                .sign(Algorithm.HMAC256(dotEnv["JWT_SECRET"]))

            val expirationTime = LocalDateTime.now().plus(Duration.ofMinutes(30)) // Set expiration time to 7 days from now
            val gmtDate = GMTDate(expirationTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())

            call.response.cookies.append(Cookie(name = "JWT_TOKEN", value = token, expires = gmtDate, path = "/"))
            call.respond(hashMapOf("token" to token))
        }
    }

    post("/api/users/register") {
        val userToCreate = call.receive<User>()

        val users = userService.read(userToCreate)
        
        if (users.isEmpty() && userToCreate.username != "" && userToCreate.password != "") {
            userService.create(userToCreate)
            call.respond(HttpStatusCode.OK)
        } else if (userToCreate.username == "" || userToCreate.password == "") {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(HttpStatusCode.NotAcceptable)
        }
    }

    post("/api/users/like") {
        val req = call.receive<LikeRequest>()

        val resultStatus = userService.likeRecipe(req.userId.toInt(), req.recipeId.toInt())
        if (resultStatus) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotAcceptable)
        }
    }




//    Turned this off because no auth. Might implement again but probably not, we'll see
    /*get("/api/users/{id}") {
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
    }*/
}