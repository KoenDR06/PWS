package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Tag(val name: String)
class TagsService(private val database: Database) {
    object Tags : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 32)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tags)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    companion object {
        lateinit var INSTANCE: TagsService
    }

    suspend fun create(tag: Tag): Int = dbQuery {
        Tags.insert {
            it[name] = tag.name
        }[Tags.id]
    }

    suspend fun read(id: Int): Tag? {
        return dbQuery {
            Tags.select { Tags.id eq id }
                .map { Tag(it[Tags.name]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, tag: Tag) {
        dbQuery {
            Tags.update({ Tags.id eq id }) {
                it[name] = tag.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Tags.deleteWhere { Tags.id.eq(id) }
        }
    }
}