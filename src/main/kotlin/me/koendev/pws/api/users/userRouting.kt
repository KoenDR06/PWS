package me.koendev.pws.api.users

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import me.koendev.pws.database.User
import me.koendev.pws.dotEnv
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun Routing.userRouting() {
    post("/api/login") {
        val user = User("Koen de Ruiter", "1234")
        // Check username and password
        // ...
        val token = JWT.create()
            .withAudience(dotEnv["JWT_AUDIENCE"])
            .withIssuer(dotEnv["JWT_ISSUER"])
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60 * 30 * 1000))
            .sign(Algorithm.HMAC256(dotEnv["JWT_SECRET"]))

        val expirationTime = LocalDateTime.now().plus(Duration.ofDays(7)) // Set expiration time to 7 days from now
        val gmtDate = GMTDate(expirationTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())

        call.response.cookies.append(Cookie("JWT_TOKEN", token, expires = gmtDate))
        call.respond(hashMapOf("token" to token))
    }

    authenticate("auth-jwt") {
        get("/hello") {
            val token = call.request.cookies["JWT_TOKEN"]
            val principal = JWTPrincipal(JWT.decode(token))

            val username = principal.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
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