package com.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class StatusCheckerTest {
    private val dataStore = DataStore()
    private val statusChecker = StatusChecker(dataStore)

    private fun cleanUpFiles() {
        File("data_store.json").delete()
    }

    @Test
    fun `getWebsiteStatus should return 200 for valid URL`() {
        val validUrl = "https://www.google.com"
        val status = statusChecker.getWebsiteStatus(validUrl)
        assertEquals(200, status)
    }

    @Test
    fun `getWebsiteStatus should return -1 for invalid URL`() {
        val invalidUrl = "invalid"
        val status = statusChecker.getWebsiteStatus(invalidUrl)
        assertEquals(-1, status)
    }

    @Test
    fun `isValidUrl should return true for valid URLs`() {
        assertTrue(statusChecker.isValidUrl("http://www.example.com"))
        assertTrue(statusChecker.isValidUrl("https://www.example.org"))
        assertTrue(statusChecker.isValidUrl("ftp://ftp.example.net"))
    }

    @Test
    fun `isValidUrl should return false for invalid URLs`() {
        assertFalse(statusChecker.isValidUrl("invalid"))
        assertFalse(statusChecker.isValidUrl("example.com"))
        assertFalse(statusChecker.isValidUrl("http://"))
    }

    @Test
    fun `test loadWebsiteStatuses with page`() {
        val status1 = WebsiteStatus("https://www.example.com", 200, 1683895200000)
        val status2 = WebsiteStatus("https://www.google.com", 404, 1683895260000)
        dataStore.saveWebsiteStatuses(listOf(status1, status2))

        val status3 = WebsiteStatus("https://www.anotherexample.com", 200, 1683895300000)
        dataStore.saveWebsiteStatuses(listOf(status3))

        val statusesPage1 = dataStore.loadWebsiteStatuses(page = 1)

        assertTrue(statusesPage1.containsAll(listOf(status1, status2)))
        assertTrue(statusesPage1.size >= 2)
    }

    @Test
    fun `test backup and restore`() {
        val status1 = WebsiteStatus("https://www.example.com", 200, 1683895200000)
        val status2 = WebsiteStatus("https://www.google.com", 404, 1683895260000)
        dataStore.saveWebsiteStatuses(listOf(status1, status2))

        val backupFilePath = "backup.json"
        dataStore.backup(backupFilePath)

        val status3 = WebsiteStatus("https://www.anotherexample.com", 200, 1683895300000)
        dataStore.saveWebsiteStatuses(listOf(status3))

        dataStore.restore(backupFilePath)
        val allStatuses = dataStore.loadAllStatuses()
        assertTrue(allStatuses.isNotEmpty())
        assertTrue(allStatuses.values.flatten().containsAll(listOf(status1, status2)))
        assertTrue(allStatuses.values.flatten().contains(status3))
        cleanUpFiles()
    }

}