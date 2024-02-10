package me.koendev.pws.site.templates

import kotlinx.html.*
import me.koendev.pws.database.RecipeItem
import org.jetbrains.exposed.sql.transactions.transaction

fun FlowContent.recipeCard(recipeId: Int) {
    transaction {
        val recipe = RecipeItem.findById(recipeId)
        if (recipe != null) {
            a(href = "/recepten/${recipe.id.value}", classes = "recipe-card-link") {
                div(classes = "recipe-card") {
                    img(src = recipe.imageUrl, alt = "Afbeelding van ${recipe.title}") {
                        classes = setOf("recipe-image")
                    }
                    div(classes = "recipe-info") {
                        h2(classes = "recipe-title") { +recipe.title }
                        p(classes = "recipe-description") { +recipe.description }
                        div(classes = "recipe-stats") {
                            span(classes = "recipe-time") { +"${recipe.totalTime} min" }
                        }
                    }
                }
            }
        }
    }
}