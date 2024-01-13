package me.koendev.pws.site

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import me.koendev.pws.data.Recipe
import readInput

fun Routing.recept() {
    val dataLocation = "static/new_recipe_data.json"
    val recipes = Json.decodeFromString<List<Recipe>>(readInput(dataLocation).joinToString(""))



}


