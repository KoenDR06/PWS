//package me.koendev.pws.singleuse
//
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.json.Json
//import me.koendev.pws.database.Tag
//import me.koendev.pws.database.TagsService
//import me.koendev.pws.dotEnv
//import me.koendev.pws.plugins.database
//import org.jetbrains.exposed.sql.Database
//import readInput
//
//
//fun main() {
//    database = Database.connect(
//        url = dotEnv["DB_URL"],
//        user = dotEnv["DB_USER"],
//        driver = "org.mariadb.jdbc.Driver",
//        password = dotEnv["DB_PASSWORD"]
//    )
//
//    val dataLocation = "static/tag_data.json"
//    val tags = Json.decodeFromString<Map<String, String>>(readInput(dataLocation).joinToString(""))
//
//    runBlocking {
//        val db = TagsService(database = database)
//        for(tag in tags) {
//                db.create(Tag(tag.key))
//        }
//    }
//}