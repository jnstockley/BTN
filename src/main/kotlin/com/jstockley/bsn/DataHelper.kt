package com.jstockley.bsn
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import mu.KotlinLogging
import org.json.JSONException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset

val logger = KotlinLogging.logger{}

private fun getData(path: String): String {
    val file = File(path)
    if (!file.exists()) {
        logger.error { "$path is not found!" }
        throw FileNotFoundException("$path is not found!")
    }
    return file.readText(Charset.defaultCharset())
}

fun getDataAsList(path: String): List<String> {
    return ObjectMapper().readValue(getData(path))
}

fun getDataAsIntMap(path: String): Map<String, Int> {
    return ObjectMapper().readValue(getData(path))
}

fun getDataAsStringMap(path: String): Map<String, String> {
    return ObjectMapper().readValue(getData(path))
}

fun getDataAsBooleanMap(path: String): Map<String, Boolean> {
    return ObjectMapper().readValue(getData(path))
}


fun writeData(path: String, data: Any) {
    val json: String
    val file = File(path)
    if (!file.exists()) {
        val pattern = Regex("(/[a-zA-Z-]*.json)")
        val fileName = pattern.find(path)!!.value
        val dirs = File(path.substring(0, path.indexOf(fileName)))
        dirs.mkdirs()
        file.createNewFile()
    }
    try {
        json = Gson().toJson(data)
    } catch (e: JSONException) {
        logger.error { "$data is not valid JSON!" }
        throw JSONException("$data is not valid JSON!")
    }
    PrintWriter(FileWriter(file)).use {
        it.write(json)
    }
    logger.debug { "$json wrote JSON to $file" }
}