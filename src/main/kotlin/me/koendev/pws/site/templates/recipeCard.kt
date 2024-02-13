package me.koendev.pws.site.templates

import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import me.koendev.pws.database.RecipeItem
import me.koendev.pws.database.UserItem
import org.jetbrains.exposed.sql.transactions.transaction

fun FlowContent.recipeCard(recipeId: Int, userId: Int) {
    transaction {
        runBlocking {
            val recipe = RecipeItem.findById(recipeId)
            val user = UserItem.findById(userId)

            if (recipe != null && user != null) {
                val imgSrc = if (recipe.id.value in user.likedRecipes) {
                    "/static/images/liked.svg"
                } else {
                    "/static/images/like.svg"
                }
                div(classes = "recipe-card") {
                    a(href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                        img(src = recipe.imageUrl, alt = "Afbeelding van ${recipe.title}") {
                            classes = setOf("recipe-image")
                        }
                    }
                    div(classes = "recipe-info") {
                        a(href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                            h2(classes = "recipe-title") { +recipe.title }
                            p(classes = "recipe-description") { +recipe.description }
                        }
                        div(classes = "recipe-stats") {
                            span(classes = "recipe-time") { +"${recipe.totalTime} min" }
                            button {
                                onClick = "likeRecipe(\"$userId\", \"${recipe.id.value}\")"
                                img {
                                    style = "width: 1.5cm; height: auto;"
                                    src = imgSrc
                                    id = "heart-image-${recipe.id.value}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}