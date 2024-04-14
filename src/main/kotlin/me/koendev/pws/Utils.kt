package me.koendev.pws

import io.ktor.http.*
import kotlinx.serialization.Serializable
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/main/resources", name)
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.sha256() = BigInteger(1, MessageDigest.getInstance("SHA256").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

val HttpStatusCode.Companion.IAmATeaPot get() = HttpStatusCode(418, "I'm a tea pot")


@Serializable
data class LikeRequest(val userId: String, val recipeId: String)