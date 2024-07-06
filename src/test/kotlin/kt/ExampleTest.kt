package kt

import me.saro.selenium.ChromeDriverManager
import me.saro.selenium.model.DownloadStrategy.DOWNLOAD_IF_NO_VERSION
import me.saro.selenium.model.Platform.Companion.getPlatform
import org.junit.jupiter.api.Test
import java.io.File

// @Disabled
class ExampleTest {
    @Test
    fun test() {
        val manager = ChromeDriverManager.builder(File("./tmp"))
            .downloadStrategy(DOWNLOAD_IF_NO_VERSION) // default value
            .enableRecommendChromeOptions(true)
            .build()

        val list = manager.openBackground("https://gs.saro.me") {
            finds(".post-list .node")
                .map { it.find("a").text.trim() + " : " + it.find("a").getAttribute("href") }
        }

        list.forEach(::println)
    }

    @Test
    fun download() {
        ChromeDriverManager.download(File("./tmp"), getPlatform(), DOWNLOAD_IF_NO_VERSION)
    }
}