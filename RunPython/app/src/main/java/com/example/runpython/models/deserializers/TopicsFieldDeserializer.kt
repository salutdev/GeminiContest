package com.example.runpython.models.deserializers

import com.google.gson.*
import java.lang.reflect.Type

class TopicsFieldDeserializer : JsonDeserializer<String> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String {
            return when {
            json.isJsonArray -> {
                json.asJsonArray.map { it.asString }.joinToString(separator = ", ")
            }
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                json.asString
            }
            else -> {
                throw JsonParseException("Expected a JSON Array or String but got ${json::class.java.simpleName}")
            }
        }
    }
}