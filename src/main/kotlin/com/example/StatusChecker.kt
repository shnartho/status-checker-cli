package com.example

import java.net.URL
import java.net.HttpURLConnection
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatusChecker {

    private val dataStore = DataStore()

    fun run(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: statuscheck <command> [options]")
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
        val urls = if (args.isEmpty()) dataStore.loadWebsiteConfigs().map { it.url } else args.toList()
        val statuses = urls.map { WebsiteStatus(it, getWebsiteStatus(it), System.currentTimeMillis()) }
        dataStore.saveWebsiteStatuses(statuses)
        println("Successfully fetched statuses for: ${urls.joinToString(", ")}")
    }

    private fun live(args: Array<String>) {
        val urls = if (args.isEmpty()) dataStore.loadWebsiteConfigs().map { it.url } else args.toList()

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            val statuses = urls.map { WebsiteStatus(it, getWebsiteStatus(it), System.currentTimeMillis()) }
            dataStore.saveWebsiteStatuses(statuses)
            println("Live status updates for: ${urls.joinToString(", ")}")
        }, 0, 5, TimeUnit.SECONDS)

        println("Live monitoring started for: ${urls.joinToString(", ")}")
        println("Press Ctrl+C to stop monitoring")
        readlnOrNull()
        executor.shutdown()
    }

    private fun history(args: Array<String>) {
        val urls = if (args.isEmpty()) dataStore.loadWebsiteConfigs().map { it.url } else args.toList()
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

    private fun getWebsiteStatus(url: String): Int {
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
}

fun main(args: Array<String>) {
    StatusChecker().run(args)
}
