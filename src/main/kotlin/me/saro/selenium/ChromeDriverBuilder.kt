package me.saro.selenium


import me.saro.selenium.model.DownloadStrategy
import me.saro.selenium.model.PathManager
import me.saro.selenium.model.SeleniumChromeException
import me.saro.selenium.service.ChromeDriverPlus
import me.saro.selenium.service.ChromeManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File
import java.time.Duration
import java.util.UUID
import java.util.logging.Logger

class ChromeDriverBuilder internal constructor(
    private val manageChromePath: File,
) {
    companion object {
        private var created = false
    }

    private var downloadStrategy: DownloadStrategy = DownloadStrategy.DOWNLOAD_IF_NO_VERSION
    private val options: MutableSet<String> = mutableSetOf()
    private val properties: MutableMap<String, String> = mutableMapOf()
    private val log = Logger.getLogger(ChromeDriverBuilder::class.qualifiedName)

    fun downloadStrategy(downloadStrategy: DownloadStrategy): ChromeDriverBuilder {
        this.downloadStrategy = downloadStrategy
        return this
    }

    fun option(option: String): ChromeDriverBuilder {
        assert (option.isNotBlank()) { "option is blank" }
        val lof = option.lastIndexOf('=')
        if (lof != -1) {
            val key = option.substring(0, lof)
            options.removeIf { it.startsWith(key) }
        }
        if (option.startsWith("--headless")) {
            throw SeleniumChromeException("The --headless option cannot be used here.\nYou can use the methods openBackground(), openWith(), or newChromeDriver() through ChromeDriverManager.")
        }
        options.add(option)
        return this
    }

    fun enableRecommendChromeOptions(disabledSecurity: Boolean): ChromeDriverBuilder {
        option("--user-data-dir=${System.getProperty("java.io.tmpdir")}/saro-caio/{{UUID}}") // Prevents socket errors.
            .option("--disable-infobars") // Disables browser information bar.
            .option("--disable-dev-shm-usage") // Ignores the limit on temporary disk space for the browser.
            .option("--blink-settings=imagesEnabled=false") // Disables image loading.
            .option("--disable-extensions")
            .option("--disable-search-engine-choice-screen")
            .option("--disable-popup-blocking")
            .option("--disable-gpu")
        if (disabledSecurity) {
            properties["webdriver.chrome.whitelistedIps"] = ""
            option("--no-sandbox")
                .option("--ignore-certificate-errors")
        }
        return this
    }

    @Synchronized
    fun build(): ChromeDriverManager {
        if (created) {
            log.warning("SeleniumAllInOne is already created.\nIt is a singleton object.")
        }
        val pathManager = PathManager.create(manageChromePath)
        ChromeManager.load(pathManager, downloadStrategy)
        properties["webdriver.chrome.driver"] = pathManager.chromedriverBinPath
        properties.forEach(System::setProperty)
        created = true
        return ChromeDriverManagerImpl(pathManager.chromeBinPath, options.toSet())
    }

    class ChromeDriverManagerImpl(
        private val chromeBinPath: String,
        private val options: Set<String>,
    ): ChromeDriverManager {
        private val defaultTimeout: Duration = Duration.ofSeconds(20)

        override fun <T> openWith(url: String, addOption: Set<String>, use: ChromeDriverPlus.() -> T): T {
            val driver = newChromeDriver(createChromeOptions(addOption))
            var cdp:ChromeDriverPlus? = null
            try {
                cdp = ChromeDriverPlus(driver).apply {
                    windowSize(2000, 3000)
                    implicitWaitTimeout = defaultTimeout
                    pageLoadTimeout = defaultTimeout
                    move(url)
                }
                return use(cdp)
            } finally {
                cdp?.terminate()
            }
        }

        override fun newChromeDriver(chromeOptions: ChromeOptions): ChromeDriver =
            ChromeDriver(chromeOptions.setBinary(chromeBinPath))

        private fun createChromeOptions(addOption: Set<String>): ChromeOptions =
            ChromeOptions().apply {
                (options + addOption).forEach {
                    addArguments(it.replace("{{UUID}}", UUID.randomUUID().toString()))
                }
            }
    }
}