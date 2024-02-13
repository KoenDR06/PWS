package me.koendev.pws.site

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.koendev.pws.site.templates.navBar

fun Routing.registreren() {
    get("/registreren") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                title {
                    +"Registreren"
                }
                link (rel = "stylesheet", href = "/static/styles/receptenStyle.css", type = "text/css")
                link (rel = "stylesheet", href = "/static/styles/navBar.css", type = "text/css")
                link (rel = "icon", href = "/static/images/favicon.ico", type = "image/x-icon")
                script (src = "/static/scripts/post-register-data.js") {}
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
                        value = "Registreren"
                        onClick = "submitRegisterForm()"
                    }
                }
                p {
                    id = "status-text"
                }
            }
        }
    }
}