package kt

import me.saro.selenium.ChromeDriverManager
import me.saro.selenium.model.DownloadStrategy.DOWNLOAD_IF_NO_VERSION
import me.saro.selenium.model.Platform.Companion.getPlatform
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled
class ExampleTest {
    @Test
    fun test() {
        val manager = ChromeDriverManager.builder(File("./tmp"))
            .downloadStrategy(DOWNLOAD_IF_NO_VERSION) // default value
            .enableRecommendChromeOptions(true)
            .build()

        val list = manager.openBackground("https://anissia.net") {
            finds(".flex.items-center.py-3.my-1.border-b.border-gray-200.text-sm.anissia-home-reduce-10")
                .map { it.find("a").text }
        }

        list.forEach(::println)
    }

    @Test
    fun download() {
        ChromeDriverManager.download(File("./tmp"), getPlatform(), DOWNLOAD_IF_NO_VERSION)
    }
}