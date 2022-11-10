package com.shares.app.util


import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException


class IntTypeAdapter(): TypeAdapter<Number>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Number?) {
        out.value(value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Number? {
        if (`in`.peek() === JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        return try {
            val result: String = `in`.nextString()
            if ("" == result) {
                null
            } else result.toInt()
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}