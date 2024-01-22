package me.koendev.pws.site

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.plugins.recipeService

fun Routing.webRouting() {
    recepten(recipeService)
    recept(recipeService)
    login()
    search()
    mealplan()
    voorkeuren()

    get("/") {
        call.respondRedirect("/recepten")
    }
}