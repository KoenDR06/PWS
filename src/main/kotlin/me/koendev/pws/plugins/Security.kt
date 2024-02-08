package me.koendev.pws.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.koendev.pws.dotEnv

fun Application.configureSecurity() {
    val secret = dotEnv["JWT_SECRET"]
    val issuer = dotEnv["JWT_ISSUER"]
    val audience = dotEnv["JWT_AUDIENCE"]
    val myRealm = dotEnv["JWT_REALM"]
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())
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
