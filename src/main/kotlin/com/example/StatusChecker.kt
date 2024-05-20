package com.example

import java.net.URL
import java.net.HttpURLConnection
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatusChecker(private val dataStore: DataStore) {

    fun run(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: StatusChecker <command> [options]")
            println("Available commands: fetch, live, history, backup, restore")
            return
        }

        when (args[0]) {
            "fetch" -> fetch(args.sliceArray(1..args.lastIndex))
            "live" -> live(args.sliceArray(1..args.lastIndex))
            "history" -> history(args.sliceArray(1..args.lastIndex))
            "backup" -> backup(args.sliceArray(1..args.lastIndex))
            "restore" -> restore(args.sliceArray(1..args.lastIndex))
            else -> {
                println("Invalid command: ${args[0]}")
            }
        }
    }

    private fun fetch(args: Array<String>) {
        val showResult = args.contains("--show-result")
        val subsetArgument = args.find { it.startsWith("--subset=") }
        val subsetCount = subsetArgument?.substringAfter("=")?.toIntOrNull() ?: Int.MAX_VALUE

        val urls = dataStore.loadWebsiteConfigs().map { it.url }.take(subsetCount)

        if (urls.isEmpty()) {
            println("No URLs configured in the datastore.")
            return
        }

        val validUrls = urls.filter { isValidUrl(it) }
        val statuses = validUrls.mapNotNull { url ->
            val status = getWebsiteStatus(url)
            if (status != -1) {
                if (showResult) {
                    println("$url: $status")
                }
                WebsiteStatus(url, status, System.currentTimeMillis())
            } else {
                null
            }
        }

        dataStore.saveWebsiteStatuses(statuses)

        if (statuses.isNotEmpty()) {
            println("Successfully fetched statuses for: ${statuses.joinToString(", ") { it.url }}")
        } else {
            println("No valid URLs provided or fetched successfully. Example of a valid URL: https://www.example.com")
        }
    }

    private fun live(args: Array<String>) {
        val showResult = args.contains("--show-result")
        val subsetArgument = args.find { it.startsWith("--subset=") }
        val subsetCount = subsetArgument?.substringAfter("=")?.toIntOrNull() ?: Int.MAX_VALUE

        val intervalArgument = args.find { it.startsWith("--interval=") }
        val intervalSeconds = intervalArgument?.substringAfter("=")?.toLongOrNull() ?: 5L

        val urls = dataStore.loadWebsiteConfigs().map { it.url }.take(subsetCount)

        if (urls.isEmpty()) {
            println("No URLs configured in the datastore.")
            return
        }

        val validUrls = urls.filter { isValidUrl(it) }

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            val statuses = validUrls.map { url ->
                val status = getWebsiteStatus(url)
                if (showResult) {
                    println("$url: $status")
                }
                WebsiteStatus(url, status, System.currentTimeMillis())
            }
            dataStore.saveWebsiteStatuses(statuses)
            if (showResult) {
                println("Live status updates for: ${validUrls.joinToString(", ")}")
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS)

        println("Live monitoring started for: ${validUrls.joinToString(", ")} with an interval of $intervalSeconds seconds.")
        println("Press Ctrl+C to stop monitoring")
        readlnOrNull()
        executor.shutdown()
    }

    private fun history(args: Array<String>) {
        val pageArgument = args.find { it.startsWith("--page=") }
        val pageNumber = pageArgument?.substringAfter("=")?.toIntOrNull()

        if (pageNumber != null) {
            val pageSize = 10
            val allStatuses = dataStore.loadAllStatuses()
            val pageData = allStatuses[pageNumber] ?: emptyList()
            //val startIdx = (pageNumber - 1) * pageSize
            //val endIdx = minOf(startIdx + pageSize, allStatuses.values.flatten().size)
            val totalPages = (allStatuses.values.flatten().size + pageSize - 1) / pageSize
            if (pageData.isNotEmpty()) {
                println("Page $pageNumber/$totalPages:")
                println("+----------------------------------------------------------------------+")
                println("|                      URL | Status | Timestamp")
                println("|----------------------------------------------------------------------|")
                pageData.forEach {
                    println("| ${it.url} | ${it.status} | ${Date(it.timestamp)}")
                }
                println("+----------------------------------------------------------------------+")
            } else {
                println("No data found for page $pageNumber.")
            }
            return
        }

        val urls = if (args.isEmpty()) dataStore.loadWebsiteConfigs().map { it.url } else args.filter { !it.startsWith("--page=") }.toList()
        val history = dataStore.loadWebsiteStatuses().filter { urls.contains(it.url) }

        if (history.isEmpty()) {
            println("No history found for the specified URLs")
            return
        }

        println("Website History:")
        println("+----------------------------------------------------------------------+")
        println("|                      URL | Status | Timestamp")
        println("|----------------------------------------------------------------------|")
        history.forEach {
            println("| ${it.url} | ${it.status} | ${Date(it.timestamp)}")
        }
        println("+----------------------------------------------------------------------+")
    }


    private fun backup(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: backup <backup_file_path>")
            return
        }

        val backupFilePath = args[0]
        dataStore.backup(backupFilePath)
    }

    private fun restore(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: restore <backup_file_path>")
            return
        }

        val backupFilePath = args[0]
        dataStore.restore(backupFilePath)
    }

    fun getWebsiteStatus(url: String): Int {
        if (!isValidUrl(url)) {
            println("Error: Invalid URL: $url")
            return -1
        }

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            return connection.responseCode
        } catch (e: Exception) {
            println("Error connecting to $url: ${e.message}")
            return -1
        }
    }

    fun isValidUrl(url: String): Boolean {
        val urlPattern = Regex("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        return urlPattern.matches(url)
    }

}

fun main(args: Array<String>) {
    val dataStore = DataStore()
    StatusChecker(dataStore).run(args)
}
