package com.mamm.mammapps.remote

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class NullStringAdapter : TypeAdapter<String?>() {
    override fun write(out: JsonWriter, value: String?) {
        out.value(value)
    }

    override fun read(reader: JsonReader): String? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val value = reader.nextString()
        return if (value.equals("null", ignoreCase = true)) null else value
    }
}