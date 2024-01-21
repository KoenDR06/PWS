package me.koendev.pws.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Tag(val name: String)


class TagItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TagItem>(TagsService.Tags)

    var name by TagsService.Tags.name
}


class TagsService(database: Database) {
    object Tags : IntIdTable() {
        val name = varchar("name", length = 32)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tags)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun delete(id: Int) {
        dbQuery {
            Tags.deleteWhere { Tags.id.eq(id) }
        }
    }

    suspend fun create(tag: Tag) {
        dbQuery {
            TagItem.new {
                name = tag.name
            }
        }
    }
}