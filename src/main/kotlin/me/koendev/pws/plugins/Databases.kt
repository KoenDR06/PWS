package me.koendev.pws.plugins

import io.ktor.server.application.*
import me.koendev.pws.database.Recipe
import me.koendev.pws.database.UserService
import me.koendev.pws.dotEnv
import org.jetbrains.exposed.sql.Database

lateinit var database: Database

fun Application.configureDatabases() {
    database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        driver = "org.mariadb.jdbc.Driver",
        password = dotEnv["DB_PASSWORD"]
    )

    val userService = UserService(database)
    val recipe = Recipe(database)

    UserService.INSTANCE = userService
    Recipe.INSTANCE = recipe
}
