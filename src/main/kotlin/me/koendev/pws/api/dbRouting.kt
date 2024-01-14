package me.koendev.pws.api

import io.ktor.server.routing.*
import me.koendev.pws.api.recipes.recipeRouting
import me.koendev.pws.api.users.userRouting

fun Routing.dbRouting() {
    userRouting()
    recipeRouting()
}