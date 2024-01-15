package me.koendev.pws.plugins

import io.ktor.server.application.*
import me.koendev.pws.database.RecipeService
import me.koendev.pws.database.RecipeTagsService
import me.koendev.pws.database.TagsService
import me.koendev.pws.database.UserService
import me.koendev.pws.dotEnv
import org.jetbrains.exposed.sql.Database

lateinit var database: Database

lateinit var recipeService: RecipeService
lateinit var userService: UserService
lateinit var tagsService: TagsService
lateinit var recipeTagsService: RecipeTagsService

fun Application.configureDatabases() {
    database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        driver = "org.mariadb.jdbc.Driver",
        password = dotEnv["DB_PASSWORD"]
    )

    userService = UserService(database)
    recipeService = RecipeService(database)
    tagsService = TagsService(database)
    recipeTagsService = RecipeTagsService(database)
}
