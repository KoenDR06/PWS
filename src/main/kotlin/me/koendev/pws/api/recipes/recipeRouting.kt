package me.koendev.pws.api.recipes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.koendev.pws.database.Recipe
import me.koendev.pws.database.RecipeService
import me.koendev.pws.plugins.database
import println

fun Routing.recipeRouting() {
    val baseUrl = "/api/recipes"

    get("${baseUrl}/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("${baseUrl}/${userId}")
    }
    get("${baseUrl}/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val recipe = RecipeService(database = database).read(id)
        if (recipe != null) {
            call.respond(HttpStatusCode.OK, recipe)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("${baseUrl}/") {
        call.respondRedirect(baseUrl)
    }
    post(baseUrl) {
        val recipe = call.receive<Recipe>()
        val id = RecipeService(database = database).create(recipe)
        call.respond(HttpStatusCode.OK, id)
    }

    delete("${baseUrl}/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("${baseUrl}/${userId}")
    }
    delete("${baseUrl}/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            RecipeService(database = database).delete(id)
        } catch(e: Exception) {
            e.println()
        }
    }

    put("${baseUrl}/{id}/") {
        val userId = call.parameters["id"]
        call.respondRedirect("${baseUrl}/${userId}")
    }
    put("${baseUrl}/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        try {
            val recipe = call.receive<Recipe>()
            RecipeService(database = database).update(id, recipe = recipe)
            call.respond(HttpStatusCode.OK, id)
        } catch(e: Exception) {
            e.println()
        }
    }
}