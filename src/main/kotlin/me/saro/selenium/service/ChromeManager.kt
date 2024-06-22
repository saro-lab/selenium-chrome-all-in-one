package me.saro.selenium.service

import me.saro.selenium.comm.Utils
import me.saro.selenium.model.ChromeVersionDetails
import me.saro.selenium.model.DownloadStrategy
import me.saro.selenium.model.PathManager
import me.saro.selenium.model.SeleniumChromeException
import java.io.File
import java.net.URI

class ChromeManager {
    companion object {
        private val log = Utils.getLogger(ChromeManager::class)

        fun load(pathManager: PathManager, downloadStrategy: DownloadStrategy) {
            log.info("ChromeManager.load(): downloadStrategy: $downloadStrategy")
            if (pathManager.existsBinaries) {
                if (downloadStrategy == DownloadStrategy.DOWNLOAD_IF_NO_VERSION) {
                    log.info("find chrome binaries in ${pathManager.chromeRoot}")
                    return
                }
            } else if (downloadStrategy == DownloadStrategy.THROW_IF_NO_VERSION) {
                throw SeleniumChromeException("cannot find chrome binaries in ${pathManager.chromeRoot}\nbut your downloadStrategy is ${DownloadStrategy.THROW_IF_NO_VERSION}")
            }

            val chromeVersionDetails = ChromeVersionDetails(pathManager.platform.value)
            if (pathManager.existsBinaries && pathManager.revision == chromeVersionDetails.revision) {
                log.info("find chrome binaries in ${pathManager.chromeRoot}")
                return
            }

            File(pathManager.platformRoot, "${pathManager.chromePrefix}${chromeVersionDetails.revision}").mkdirs()
            log.info("chrome binaries not found in ${pathManager.chromeRoot} and download ready")
            install(URI(chromeVersionDetails.chromeDriverUri), "#chromedriver.zip", pathManager.chromeRoot)
            install(URI(chromeVersionDetails.chromeUri), "#chrome.zip", pathManager.chromeRoot)
            if (pathManager.existsBinaries) {
                log.info("installed chrome binaries in ${pathManager.chromeRoot}")
            } else {
                throw SeleniumChromeException("download and unzip completed, but not found binaries in ${pathManager.chromeRoot}")
            }
        }

        private fun install(uri: URI, save: String, root: String) {
            try {
                log.info("download $save start...")
                URI.create(uri.toString()).toURL().openStream().use { input -> File(root, save).outputStream().use(input::copyTo) }
                log.info("download $save done and unzip start...")
                Utils.unzip(File(root, save), 1, File(root))
                log.info("download $save done and unzip done")
                File(root, save).delete()
            } catch (e: Exception) {
                throw SeleniumChromeException("failed to install chrome binaries", e)
            }
        }
    }
}