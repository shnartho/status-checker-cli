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
    private val dataStoreFile = File("data_store.json")
    private val pageSize = 10

    fun saveWebsiteStatuses(websiteStatuses: List<WebsiteStatus>) {
        val allStatuses = loadAllStatuses()
        val latestPage = allStatuses.keys.maxOrNull() ?: 1
        var currentPage = latestPage
        var currentPageStatuses = allStatuses[currentPage]?.toMutableList() ?: mutableListOf()

        websiteStatuses.forEach { status ->
            if (currentPageStatuses.size >= pageSize) {
                currentPage++
                currentPageStatuses = mutableListOf()
            }
            currentPageStatuses.add(status)
            allStatuses[currentPage] = currentPageStatuses
        }

        try {
            FileWriter(dataStoreFile).use { writer ->
                gson.toJson(allStatuses, writer)
            }
        } catch (e: Exception) {
            println("Error saving website statuses: ${e.message}")
        }
    }

    fun loadWebsiteStatuses(page: Int? = null): List<WebsiteStatus> {
        return if (page != null) {
            val allStatuses = loadAllStatuses()
            allStatuses[page] ?: emptyList()
        } else {
            loadAllStatuses().values.flatten()
        }
    }

    fun loadAllStatuses(): MutableMap<Int, List<WebsiteStatus>> {
        return try {
            if (dataStoreFile.exists()) {
                gson.fromJson(
                    FileReader(dataStoreFile),
                    object : TypeToken<MutableMap<Int, List<WebsiteStatus>>>() {}.type
                )
            } else {
                mutableMapOf()
            }
        } catch (e: Exception) {
            println("Error loading website statuses: ${e.message}")
            mutableMapOf()
        }
    }

    fun loadWebsiteConfigs(): List<WebsiteConfig> {
        val configFile = File("web_config.json")
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
        val allStatuses = loadAllStatuses()
        val json = gson.toJson(allStatuses)
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
            val backupData: MutableMap<Int, List<WebsiteStatus>> = gson.fromJson(
                FileReader(backupFilePath),
                object : TypeToken<MutableMap<Int, List<WebsiteStatus>>>() {}.type
            )

            val currentDataStore = loadAllStatuses()

            if (backupData.isNotEmpty() && currentDataStore.isNotEmpty()) {
                val backupFirstEntry = backupData.entries.first()
                val currentFirstEntry = currentDataStore.entries.first()
                if (backupFirstEntry.key.javaClass != currentFirstEntry.key.javaClass ||
                    backupFirstEntry.value.javaClass != currentFirstEntry.value.javaClass
                ) {
                    println("Error: Backup and current data store have different file formats. Restore aborted.")
                    return
                }
            }

            val latestPage = currentDataStore.keys.maxOrNull() ?: 0

            var currentPage = latestPage
            var currentPageItems = currentDataStore.getOrDefault(currentPage, emptyList()).toMutableList()

            backupData.values.flatten().forEach { status ->
                if (currentPageItems.size >= pageSize) {
                    currentPage++
                    currentPageItems = mutableListOf()
                }
                currentPageItems.add(status)
                currentDataStore[currentPage] = currentPageItems
            }

            FileWriter(dataStoreFile).use { writer ->
                gson.toJson(currentDataStore, writer)
            }
            println("Restore completed successfully from: $backupFilePath")
        } catch (e: Exception) {
            println("Error restoring from backup: ${e.message}")
        }
    }



}
