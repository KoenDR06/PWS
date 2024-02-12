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
import me.koendev.pws.site.templates.navBar
import org.jetbrains.exposed.sql.select

fun Routing.login() {
    get("/login") {
        var username = call.request.queryParameters["username"]
        if(username == null) {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                    link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                    link (rel = "icon", href = "/favicon.ico", type = "image/x-icon")
                    script (src = "/static/scripts/post-login-data.js") {}
                    script (src = "/static/scripts/redirect-register.js") {}
                    title {
                        +"Inloggen"
                    }
                }
                body {
                    navBar("login")

                    form {
                        input(type = InputType.text, name = "username") {
                            id = "username"
                            placeholder = "Gebruikersnaam"
                        }
                        br {}
                        input(type = InputType.password, name = "password") {
                            id = "password"
                            placeholder = "Wachtwoord"

                        }
                        br {}
                        input(type = InputType.button) {
                            value = "Log in"
                            onClick = "submitLoginForm()"
                        }
                        input(type = InputType.button) {
                            value = "Registreren"
                            onClick = "redirectRegister()"
                        }
                    }

                    h3 {
                        id = "status-text"
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
                        link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                        link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                        title {
                            +"Ingelogd"
                        }
                    }
                    body {
                        navBar("login")

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