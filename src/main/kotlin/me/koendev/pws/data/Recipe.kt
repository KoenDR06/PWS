package me.koendev.pws.data

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("/recepten")
@Serializable
data class Recipe(val id: String, val title: String, val description: String, val image_url: String,
                  val time_to_prepare: String, val oven_time: String, val waiting_time: String, val tags: List<String>,
                  val rating: Double, val ratings_count: String, val ingredients: List<Map<String, String>>,
                  val steps: List<String>)