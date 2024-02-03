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
data class Ingredient(val name: String, val amount: Double?, val unit: String)


class IngredientItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<IngredientItem>(IngredientsService.Ingredients)

    var name by IngredientsService.Ingredients.name
    var amount by IngredientsService.Ingredients.amount
    var unit by IngredientsService.Ingredients.unit
}


class IngredientsService(database: Database) {
    object Ingredients : IntIdTable() {
        val name = varchar("name", length = 128)
        val amount = double("amount").nullable()
        val unit = varchar("unit", length = 16)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Ingredients)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun delete(id: Int) {
        dbQuery {
            Ingredients.deleteWhere { Ingredients.id.eq(id) }
        }
    }

    suspend fun create(ingredient: Ingredient): Int {
        return dbQuery {
            val newIngredientId = IngredientItem.new {
                name = ingredient.name
                amount = ingredient.amount
                unit = ingredient.unit
            }.id.value

            newIngredientId
        }
    }

    suspend fun read(id: Int) : IngredientItem? {
        return dbQuery {
            Ingredients.select { Ingredients.id eq id }
                .map { IngredientItem(EntityID(id, Ingredients)) }
        }.firstOrNull()
    }
}