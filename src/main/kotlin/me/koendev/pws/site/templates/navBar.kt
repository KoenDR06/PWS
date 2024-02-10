package me.koendev.pws.site.templates

import kotlinx.html.*

fun FlowContent.navBar(activePage: String = "") {
    div("nav-bar") {
        a(classes = if (activePage == "home") "active left-align" else "left-align") {
            href = "/"
            +"Homepagina"
        }
        a(classes = if (activePage == "recepten") "active left-align" else "left-align") {
            href = "/recepten"
            +"Recepten"
        }
        a(classes = if (activePage == "mealplan") "active left-align" else "left-align") {
            href = "/mealplan"
            +"Mealplan"
        }
        a (classes = if (activePage == "voorkeuren") "active left-align" else "left-align") {
            href = "/voorkeuren"
            +"Voorkeuren"
        }


        a (classes = if (activePage == "login") "active right-align" else "right-align") {
            href = "/login"
            +"Login"
        }
        form (action = "/zoeken", classes = "right-align search-div") {
            input(type = InputType.text, name = "query", classes = "search-box") {
                placeholder = "Zoek hier naar een recept:"
            }
        }
    }
}