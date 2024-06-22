package me.saro.selenium.service

import me.saro.selenium.comm.Utils
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import java.time.Duration

class ChromeDriverPlus(
    val driver: ChromeDriver
) {
    private val log = Utils.getLogger(ChromeDriverPlus::class)

    fun move(url: String) {
        log.info("connect to $url")
        driver.get(url)
    }
    fun scrollDown(size: Int) {
        script("window.scrollBy(0, $size)")
    }
    fun scrollDown() {
        script("window.scrollBy(0, document.body.scrollHeight)")
    }
    fun script(script: String): Any? = (driver as JavascriptExecutor).executeScript(script)
    fun sleep(millis: Long) = Thread.sleep(millis)

    fun windowSize(width: Int, height: Int) {
        driver.manage().window().size = org.openqa.selenium.Dimension(width, height)
    }

    fun find(css: String): WebElement = driver.findElement(By.cssSelector(css))
    fun finds(css: String): List<WebElement> = driver.findElements(By.cssSelector(css))
    fun findsNotWait(css: String): List<WebElement> = driver.findsNotWait(css)
    fun hasElementsNotWait(css: String): Boolean = driver.hasElementsNotWait(css)

    fun SearchContext.find(css: String): WebElement = this.findElement(By.cssSelector(css))
    fun SearchContext.finds(css: String): List<WebElement> = this.findElements(By.cssSelector(css))
    fun SearchContext.findsNotWait(css: String): List<WebElement> = inImplicitWaitTimeout(Duration.ZERO) { this.finds(css) }
    fun SearchContext.hasElementsNotWait(css: String): Boolean = findsNotWait(css).isNotEmpty()


    var implicitWaitTimeout: Duration
        get() = driver.manage().timeouts().implicitWaitTimeout
        set(value) { driver.manage().timeouts().implicitlyWait(value) }

    var pageLoadTimeout: Duration
        get() = driver.manage().timeouts().pageLoadTimeout
        set(value) { driver.manage().timeouts().pageLoadTimeout(value) }

    fun <T> inImplicitWaitTimeout(duration: Duration, run: () -> T): T {
        val before = implicitWaitTimeout
        try {
            implicitWaitTimeout = duration
            return run()
        } catch (e : Exception) {
            throw e
        } finally {
            implicitWaitTimeout = before
        }
    }

    fun <T> inPageLoadTimeout(duration: Duration, run: () -> T): T {
        val before = pageLoadTimeout
        try {
            pageLoadTimeout = duration
            return run()
        } catch (e : Exception) {
            throw e
        } finally {
            pageLoadTimeout = before
        }
    }

    internal fun terminate() {
        try { driver.close() } catch (e: Exception) {}
        try { driver.quit() } catch (e: Exception) {}
        log.finer("End the Chromedriver.")
    }

    init {
        log.finer("Create a ChromeDriver.")
    }
}