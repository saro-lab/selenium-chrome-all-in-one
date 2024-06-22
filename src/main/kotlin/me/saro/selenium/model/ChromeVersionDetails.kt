package me.saro.selenium.model

import com.fasterxml.jackson.module.kotlin.jsonMapper
import me.saro.selenium.model.AppProps.Companion.chromeDownloadUri
import me.saro.selenium.model.AppProps.Companion.chromeVersion
import java.net.URI

class ChromeVersionDetails(
    private val platform: String
) {
    val revision: Int
    val chromeDriverUri: String
    val chromeUri: String

    init {
        val milestone = jsonMapper().readTree(URI(chromeDownloadUri).toURL()).at("/milestones/$chromeVersion")
        revision = milestone.path("revision").asInt()
        chromeDriverUri = milestone.at("/downloads/chromedriver").first { it.path("platform").asText() == platform }.path("url").asText()
        chromeUri = milestone.at("/downloads/chrome").first { it.path("platform").asText() == platform }.path("url").asText()
    }
}