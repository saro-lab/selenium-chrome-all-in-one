package me.saro.selenium.model

import java.io.File
import java.nio.file.Files
import java.util.logging.Logger

class PathManager private constructor(
    val manageChromePath: String,
    val platform: Platform,
) {
    private val log = Logger.getLogger(PathManager::class.qualifiedName)

    private val String.revision get() = substring(lastIndexOf('-') + 1).toIntOrNull() ?: 0

    val chromePrefix = "chrome-${AppProps.chromeVersion}-"

    val chromeBinName = when (platform) {
        Platform.MAC_X64, Platform.MAC_ARM64 -> "Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing"
        Platform.WINDOWS_64 -> "chrome.exe"
        else -> "chrome"
    }

    var chromedriverBinName = when (platform) {
        Platform.WINDOWS_64 -> "chromedriver.exe"
        else -> "chromedriver"
    }

    val platformRoot = "$manageChromePath/${platform.value}"

    val chromeRoot: String get() = File(platformRoot).apply { mkdirs() }
        .listFiles { f -> f.isDirectory && f.name.matches(Regex("$chromePrefix\\d+")) }
        ?.reduceOrNull { a, b -> if (a.name.revision > b.name.revision) a else b }?.let { return it.absolutePath } ?: ""

    val chromedriverBinPath get() = "$chromeRoot/$chromedriverBinName"

    val chromeBinPath get() = "$chromeRoot/$chromeBinName"

    val revision get() = chromeRoot.revision

    val existsBinaries: Boolean get() {
        if (chromeRoot.isEmpty()) {
            return false
        }

        try {
            if (!File(chromedriverBinPath).exists()) {
                log.info("$chromedriverBinPath is not exists")
                return false
            } else {
                log.info("$chromedriverBinPath is exists")
            }

            if (!Files.isExecutable(File(chromedriverBinPath).toPath())) {
                log.info("$chromedriverBinPath is not executable")
                File(chromedriverBinPath).setExecutable(true)
                log.info("set executable $chromedriverBinPath")
            } else {
                log.info("$chromedriverBinPath is executable")
            }

            if (!File(chromeBinPath).exists()) {
                log.info("$chromeBinPath is not exists")
                return false
            } else {
                log.info("$chromeBinPath is exists")
            }

            if (!Files.isExecutable(File(chromeBinPath).toPath())) {
                log.info("$chromeBinPath is not executable")
                File(chromeBinPath).setExecutable(true)
                log.info("$chromeBinPath is set executable")
            } else {
                log.info("$chromeBinPath is executable")
            }
        } catch (e: Exception) {
            log.warning("exception: $e")
            return false
        }

        return true
    }

    companion object {
        fun create(root: File, platform: Platform = Platform.getPlatform()): PathManager {
            return PathManager(root.canonicalPath, platform)
        }
    }
}
