package me.saro.selenium

import me.saro.selenium.model.DownloadStrategy
import me.saro.selenium.model.PathManager
import me.saro.selenium.model.Platform
import me.saro.selenium.service.ChromeDriverPlus
import me.saro.selenium.service.ChromeManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File

interface ChromeDriverManager {
    fun <T> openBackground(url: String, use: ChromeDriverPlus.() -> T): T =
        openWith(url, setOf("--headless"), use)

    fun <T> openBackground(url: String, addOption: Set<String>, use: ChromeDriverPlus.() -> T): T =
        openWith(url, addOption + setOf("--headless"), use)

    fun <T> openWith(url: String, use: ChromeDriverPlus.() -> T): T =
        openWith(url, setOf(), use)

    fun <T> openWith(url: String, addOption: Set<String>, use: ChromeDriverPlus.() -> T): T

    fun newChromeDriver(chromeOptions: ChromeOptions): ChromeDriver

    companion object {
        @JvmStatic
        fun builder(manageChromePath: File): ChromeDriverBuilder =
            ChromeDriverBuilder(manageChromePath)

        @JvmStatic
        fun download(manageChromePath: File, platform: Platform, downloadStrategy: DownloadStrategy) {
            if (downloadStrategy == DownloadStrategy.THROW_IF_NO_VERSION) {
                throw RuntimeException("download failed: downloadStrategy is THROW_IF_NO_VERSION")
            }
            ChromeManager.load(PathManager.create(manageChromePath, platform), downloadStrategy)
        }
    }
}