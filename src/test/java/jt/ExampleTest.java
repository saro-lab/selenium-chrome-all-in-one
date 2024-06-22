package jt;

import me.saro.selenium.ChromeDriverManager;
import me.saro.selenium.model.DownloadStrategy;
import me.saro.selenium.model.Platform;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Disabled
public class ExampleTest {

    @Test
    public void test() {
        ChromeDriverManager manager = ChromeDriverManager.builder(new File("./tmp"))
                .enableRecommendChromeOptions(true)
                .build();

        List<String> list = manager.openBackground("https://anissia.net", dp -> {
            List<String> items = new ArrayList<>();
            dp.finds(".flex.items-center.py-3.my-1.border-b.border-gray-200.text-sm.anissia-home-reduce-10").forEach(
                    e -> items.add(dp.find(e, "a").getText())
            );
            return items;
        });

        list.forEach(System.out::println);
    }

    @Test
    public void download() {
        ChromeDriverManager.download(new File("./tmp"), Platform.getPlatform(), DownloadStrategy.DOWNLOAD_IF_NO_VERSION);
    }
}
