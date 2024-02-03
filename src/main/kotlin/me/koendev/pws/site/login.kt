package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.currentUserId
import me.koendev.pws.database.UserService
import me.koendev.pws.plugins.userService
import org.jetbrains.exposed.sql.select

fun Routing.login() {
    get("/login") {
        var username = call.request.queryParameters["username"]
        if(username == null) {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +"Inloggen"
                    }
                }
                body {
                    form(action = "/login") {
                        input(type = InputType.text, name = "username")
                        br {}
                        input(type = InputType.submit) {
                            value = "Log in"
                            required = true
                        }
                    }
                }
            }
        } else {
            username = username.trim()
            val userId = userService.dbQuery {
                UserService.Users.select { UserService.Users.username eq username }
                    .map { it[UserService.Users.id] }
                    .firstOrNull()
            }
            if(userId == null) {
                call.respondRedirect("/login")
            } else {
                currentUserId = -1

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"Ingelogd"
                        }
                    }
                    body {
                        h1 {
                            +"Inloggen succesvol. Welkom, $username."
                        }
                        p {
                            +"U bent ingelogd. Klik hier "
                        }
                        // This tag doesn't work for some reason, so I disabled it for now
                        /*a {
                            +" hier "
                            href = "/search"
                        }*/
                        p {
                            +"om uw weekplan samen te stellen."
                        }
                    }
                }
            }
        }
    }
}