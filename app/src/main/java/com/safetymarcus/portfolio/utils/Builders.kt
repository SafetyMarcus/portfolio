package com.safetymarcus.portfolio.utils

import org.json.JSONArray
import org.json.JSONObject

/**
 * Convenience class for building [JSONObject]s.
 *
 * e.g. JsonObjectBuilder().json {
 *     "string" to "Some string"
 *     "int" to 0
 * }
 *
 * This will return a [JSONObject] with a [String] and an [Int] property stored against the passed in keys
 */
class JsonObjectBuilder(private val json: JSONObject = JSONObject()) {
    fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
        this.build()
        return json
    }

    infix fun <T> String.to(value: T) {
        json.put(this, value)
    }
}

/**
 * Convenience class for building [JSONArray]s.
 *
 * e.g. JsonArrayBuilder().json {
 *     put("something")
 *     put("something else")
 * }
 *
 * This will return a [JSONObject] with a [String] and an [Int] property stored against the passed in keys
 */
class JsonArrayBuilder {
    val array = JSONArray()

    fun <T> put(value: T) {
        array.put(value)
    }
}
