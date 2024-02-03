package me.koendev.pws.plugins

import io.ktor.server.application.*
import me.koendev.pws.database.*
import me.koendev.pws.dotEnv
import org.jetbrains.exposed.sql.Database

lateinit var database: Database

lateinit var recipeService: RecipeService
lateinit var userService: UserService
lateinit var tagsService: TagsService
lateinit var recipeTagsService: RecipeTagsService
lateinit var ingredientsService: IngredientsService
lateinit var recipeIngredientService: RecipeIngredientService
lateinit var stepService: StepService

fun Application.configureDatabases() {
    database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        password = dotEnv["DB_PASSWORD"],
        driver = "org.mariadb.jdbc.Driver"
    )

    userService = UserService(database)
    recipeService = RecipeService(database)
    tagsService = TagsService(database)
    recipeTagsService = RecipeTagsService(database)
    ingredientsService = IngredientsService(database)
    recipeIngredientService = RecipeIngredientService(database)
    stepService = StepService(database)

}
