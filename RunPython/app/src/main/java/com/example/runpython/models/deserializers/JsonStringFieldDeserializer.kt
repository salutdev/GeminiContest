package com.example.runpython.models.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class JsonStringFieldDeserializer : JsonDeserializer<String> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String =  json.toString()
}