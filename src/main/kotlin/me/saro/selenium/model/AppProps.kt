package me.saro.selenium.model

import java.util.*

class AppProps {
    companion object {
        val seleniumVersion: String
        val chromeVersion: String
        val chromeDownloadUri: String

        init {
            val props: Properties = Properties().apply { {}.javaClass.getResourceAsStream("/application.properties").use { load(it) } }
            seleniumVersion = props.getProperty("selenium.version")
            chromeDownloadUri = props.getProperty("chrome.download.uri")
            chromeVersion = props.getProperty("chrome.version")
        }
    }
}