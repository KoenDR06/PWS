package me.koendev.pws.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.koendev.pws.dotEnv
import me.koendev.pws.println

fun Application.configureSecurity() {
    val secret = dotEnv["JWT_SECRET"]
    val issuer = dotEnv["JWT_ISSUER"]
    val audience = dotEnv["JWT_AUDIENCE"]
    val myRealm = dotEnv["JWT_REALM"]
    install(Authentication) {
        jwt("jwt") {
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                //.withAudience(audience)
                .withIssuer(issuer)
                .build())

            authHeader { call ->
                val cookieValue = call.request.cookies["JWT_TOKEN"] ?: return@authHeader null
                try {
                    cookieValue.println()
                    parseAuthorizationHeader("Bearer $cookieValue")
                } catch (cause: IllegalArgumentException) {
                    cause.message
                    null
                }
            }

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respondRedirect("/login", permanent = false)
            }
        }
    }
}