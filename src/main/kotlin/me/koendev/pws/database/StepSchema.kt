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
data class Step(val content: String, val forRecipe: Int)


class StepItem(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<StepItem>(StepService.Steps)

    var content by StepService.Steps.content
    var forRecipe by StepService.Steps.forRecipe
}


class StepService(database: Database) {
    object Steps : IntIdTable() {
        val content = varchar("content", length = 2048)
        val forRecipe = integer("for_recipe")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Steps)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun delete(id: Int) {
        dbQuery {
            Steps.deleteWhere { Steps.id.eq(id) }
        }
    }

    suspend fun create(step: Step): Int {
        return dbQuery {
            val newStepId = StepItem.new {
                content = step.content
                forRecipe = step.forRecipe
            }.id.value

            newStepId
        }
    }

    suspend fun read(recipeId: Int): List<Step> {
        return dbQuery {
            Steps.select { Steps.forRecipe eq recipeId }
                .map { Step(it[Steps.content], it[Steps.forRecipe]) }
        }
    }
}