package me.koendev.pws.datatypes

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/recepten")
@Serializable
data class Recipe(val id: String, val title: String, val description: String, val image_url: String,
                  val time_to_prepare: String, val oven_time: String, val waiting_time: String, val tags: List<String>,
                  val rating: Double, val ratings_count: String, val ingredients: List<Map<String, String>>,
                  val steps: List<String>,
//                Custom fields not in data
                  var total_time: Int = 0) {
    
    init {
        val preparing_time_int = try {
            time_to_prepare.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }
        val oven_time_int = try {
            oven_time.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }
        val waiting_time_int = try {
            waiting_time.split(" ")[0].toInt()
        } catch (e: NumberFormatException) {
            0
        }
        
        total_time = preparing_time_int + oven_time_int + waiting_time_int
    }
}