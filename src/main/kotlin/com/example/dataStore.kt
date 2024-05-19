package com.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class WebsiteStatus(val url: String, val status: Int, val timestamp: Long)
data class WebsiteConfig(val url: String)

class DataStore {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dataStoreFile = File("./src/main/resources/website_data.json")

    fun saveWebsiteStatuses(websiteStatuses: List<WebsiteStatus>) {
        val json = gson.toJson(websiteStatuses)
        try {
            FileWriter(dataStoreFile).use { writer ->
                writer.write(json)
            }
        } catch (e: Exception) {
            println("Error saving website statuses: ${e.message}")
        }
    }

    fun loadWebsiteStatuses(): List<WebsiteStatus> {
        return try {
            if (dataStoreFile.exists()) {
                gson.fromJson(
                    FileReader(dataStoreFile),
                    object : TypeToken<List<WebsiteStatus>>() {}.type
                )
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error loading website statuses: ${e.message}")
            emptyList()
        }
    }

    fun loadWebsiteConfigs(): List<WebsiteConfig> {
        val configFile = File("./src/main/resources/web_config.json")  // Change the file extension to .json
        return try {
            if (configFile.exists()) {
                gson.fromJson(
                    FileReader(configFile),
                    object : TypeToken<List<WebsiteConfig>>() {}.type
                )
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error loading website configs from JSON: ${e.message}")
            emptyList()
        }
    }

    fun backup(backupFilePath: String) {
        val websiteStatuses = loadWebsiteStatuses()
        val json = gson.toJson(websiteStatuses)
        try {
            FileWriter(backupFilePath).use { writer ->
                writer.write(json)
            }
            println("Backup created successfully at: $backupFilePath")
        } catch (e: Exception) {
            println("Error creating backup: ${e.message}")
        }
    }

    fun restore(backupFilePath: String) {
        try {
            val websiteStatuses: List<WebsiteStatus> = gson.fromJson(
                FileReader(backupFilePath),
                object : TypeToken<List<WebsiteStatus>>() {}.type
            )
            saveWebsiteStatuses(websiteStatuses)
            println("Restore completed successfully from: $backupFilePath")
        } catch (e: Exception) {
            println("Error restoring from backup: ${e.message}")
        }
    }
}
