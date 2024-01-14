package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


class Recipe(private val database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 64)
        val description = varchar("description", length = 1024)
        val image_url = varchar("image_url", length = 64)
        val prepare_time = varchar("prepare_time", length = 32)
        val oven_time = varchar("oven_time", length = 32)
        val wait_time = varchar("wait_time", length = 32)
        val rating = double("rating")
        val rating_count = integer("rating_count")


        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: Recipe
    }


}