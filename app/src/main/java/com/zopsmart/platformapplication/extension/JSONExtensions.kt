package com.zopsmart.platformapplication.extension

import org.json.JSONArray
import org.json.JSONObject

inline fun <reified T : Any> JSONObject?.extract(key: String, defaultValue: T?): T? {
    if (!isValid(key))
        return defaultValue

    return when (T::class) {
        String::class -> this!!.getString(key) as T?
        Int::class -> this!!.getInt(key) as T?
        Boolean::class -> this!!.getBoolean(key) as T?
        Long::class -> this!!.getLong(key) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

fun JSONObject?.jArray(key: String, defaultValue: JSONArray = JSONArray()): JSONArray {
    if (!isValid(key))
        return defaultValue

    return try {
        this!!.getJSONArray(key)
    } catch (e: Exception) {
        defaultValue
    }
}

fun JSONObject?.jObject(key: String, defaultValue: JSONObject = JSONObject()): JSONObject {
    if (!isValid(key))
        return defaultValue

    return try {
        this!!.getJSONObject(key)
    } catch (e: Exception) {
        defaultValue
    }
}

fun JSONObject?.isValid(key: String): Boolean {
    return this != null && has(key) && !isNull(key)
}