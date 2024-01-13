package me.koendev.pws.site

import kotlinx.html.HTML
import kotlinx.html.*
import me.koendev.pws.data.Recipe

fun HTML.recipeTemplate(recipe: Recipe) {
    head {
        title("My Template Page")
    }
    body {
        div {
            id = "header"
            +"Header content goes here"
        }
        div {
            id = "main-content"
            +"$recipe"
        }
        div {
            id = "footer"
            +"Footer content goes here"
        }
    }
}