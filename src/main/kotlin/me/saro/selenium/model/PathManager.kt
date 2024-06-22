package me.saro.selenium.model

import java.io.File

class PathManager private constructor(
    val manageChromePath: String,
    val platform: Platform,
) {
    private val String.revision get() = substring(lastIndexOf('-') + 1).toIntOrNull() ?: 0

    val chromePrefix = "chrome-${AppProps.chromeVersion}-"

    val binaryFileExt = when (platform) { Platform.WINDOWS_64 -> ".exe" else -> "" }

    val platformRoot = "$manageChromePath/${platform.value}"

    val chromeRoot: String get() = File(platformRoot).apply { mkdirs() }
        .listFiles { f -> f.isDirectory && f.name.matches(Regex("$chromePrefix\\d+")) }
        ?.reduceOrNull { a, b -> if (a.name.revision > b.name.revision) a else b }?.let { return it.absolutePath } ?: ""

    val chromedriverBinPath get() = "$chromeRoot/chromedriver$binaryFileExt"

    val chromeBinPath get() = "$chromeRoot/chrome$binaryFileExt"

    val revision get() = chromeRoot.revision

    val existsBinaries: Boolean get() = chromeRoot.isNotEmpty() && File(chromedriverBinPath).exists() && File(chromeBinPath).exists()

    companion object {
        fun create(root: File, platform: Platform = Platform.getPlatform()): PathManager {
            return PathManager(root.canonicalPath, platform)
        }
    }
}