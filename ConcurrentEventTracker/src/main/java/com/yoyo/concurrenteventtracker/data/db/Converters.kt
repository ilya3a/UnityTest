package com.yoyo.concurrenteventtracker.data.db

import androidx.room.TypeConverter
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromMap(value: Map<String, String>): String {
        return JSONObject(value).toString()
    }

    @TypeConverter
    fun toMap(value: String): Map<String, String> {
        val jsonObject = JSONObject(value)
        val map = mutableMapOf<String, String>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = jsonObject.getString(key)
        }
        return map
    }
}
