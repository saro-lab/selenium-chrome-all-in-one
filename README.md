# Selenium Chrome All-in-One
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.saro/selenium-chrome-all-in-one/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.saro/selenium-chrome-all-in-one)
[![GitHub license](https://img.shields.io/github/license/saro-lab/selenium-chrome-all-in-one.svg)](https://github.com/saro-lab/selenium-chrome-all-in-one/blob/master/LICENSE)

# Introduction

Selenium Chrome All-in-One is a library that automatically downloads Chrome/ChromeDriver and helps you use Selenium with those files.

Try the example below.

# QUICK START

## Gradle

```
compile 'me.saro:selenium-chrome-all-in-one:4.36.0.0'
```

## Maven

``` xml
<dependency>
  <groupId>me.saro</groupId>
  <artifactId>selenium-chrome-all-in-one</artifactId>
  <version>4.36.0.0</version>
</dependency>
```

# Version info
- CDP: Chrome DevTools Protocol (Version) == Chrome Browser Version

| Selenium All-in-One | CDP | JDK |
|---------------------|-----|-----|
| 4.36.0.0            | 140 | 21+ |
| 4.34.0.0            | 138 | 21+ |




## Kotlin example
```kotlin
// use example
val chromeBinPath = File("./chrome-bin")

val manager = ChromeDriverManager.builder(chromeBinPath)
    //.downloadStrategy(DOWNLOAD_IF_NO_VERSION) // default value
    .enableRecommendChromeOptions(true)
    .build()

val list = manager.openBackground("https://gs.saro.me") {
    finds(".post-list .node")
        .map { it.find("a").text.trim() + " : " + it.find("a").getAttribute("href") }
}

list.forEach(::println)
```
```kotlin
// just download
val chromeBinPath = File("./chrome-bin")
ChromeDriverManager.download(chromeBinPath, getPlatform(), DOWNLOAD_IF_NO_VERSION)
```
```kotlin
// with spring project
@Configuration
@EnableAutoConfiguration
class SeleniumConfiguration {
    @Bean
    fun getChromeDriverManager(): ChromeDriverManager =
        ChromeDriverManager
            .builder(File("./chrome-bin"))
            .enableRecommendChromeOptions(true)
            .build()
}
// use example
@Service
class ScrapTradeService(
    private val chromeDriverManager: ChromeDriverManager,
    private val tradingVolumeRepository: TradingVolumeRepository,
): ScrapTrade {
    ...
}
```

## Java example
```java
// use example
File chromeBinPath = new File("./chrome-bin");

ChromeDriverManager manager = ChromeDriverManager.builder(chromeBinPath)
        .enableRecommendChromeOptions(true)
        .build();

List<String> list = manager.openBackground("https://gs.saro.me", dp -> {
    List<String> items = new ArrayList<>();
    dp.finds(".post-list .node").forEach(
            e -> items.add(dp.find(e, "a").getText().trim() + " " + dp.find(e, "a").getAttribute("href"))
    );
    return items;
});

list.forEach(System.out::println);
```
```java
// just download
File chromeBinPath = new File("./chrome-bin");
SeleniumChromeAllInOne.download(chromeBinPath, Platform.getPlatform(), DownloadStrategy.DOWNLOAD_IF_NO_VERSION);
```

# Documentation

## class ChromeDriverManager
- This is a manager that creates `ChromeDriverPlus` and `ChromeDriver` objects, and it is a singleton class.
- It is recommended to use it by creating it as a `@Bean`.
<details>
<summary style="font-size: 14px; font-weight: bold">static fun builder(manageChromePath: File): ChromeDriverBuilder</summary>

- `manageChromePath`
    - Specify the folder to store and manage the versions of the Chrome browser and ChromeDriver.
    - To avoid conflicts, it is recommended to use a folder created exclusively for the Selenium Chrome All-In-One project.
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">static fun download(manageChromePath: File, platform: Platform, downloadStrategy: DownloadStrategy)</summary>

- `manageChromePath`
    - Specify the folder to store and manage the versions of the Chrome browser and ChromeDriver.
    - To avoid conflicts, it is recommended to use a folder created exclusively for the Selenium Chrome All-In-One project.
- `platform`
    - Specify the platform to download the `Chrome browser` and `Chrome Driver`.
    - Using Platform.getPlatform() allows you to retrieve the current platform you are using.
- `downloadStrategy`
    - `DownloadStrategy.THROW_IF_NO_VERSION`
        - Throws an error if the version does not exist.
          - For example, in a server environment where the firewall is blocking, you can set `DownloadStrategy.THROW_IF_NO_VERSION` and configure the usage environment by placing the downloaded file in a folder through `ChromeDriverManager.download()`.
    - `DownloadStrategy.DOWNLOAD_IF_NO_VERSION`
        - Downloads if the version does not exist.
    - `DownloadStrategy.DOWNLOAD_IF_NO_VERSION_OR_DIFFERENT_REVISION`
        - Downloads if the version does not exist or if the revision is different.
</details>


<details>
<summary style="font-size: 14px; font-weight: bold">fun <T> openBackground(url: String, use: ChromeDriverPlus.() -> T): T </summary>

- Open the `ChromeDriverPlus` in the background.
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun <T> openBackground(url: String, addOption: Set<String>, use: ChromeDriverPlus.() -> T): T </summary>

- Open the `ChromeDriverPlus` in the background.
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun <T> openWith(url: String, use: ChromeDriverPlus.() -> T): T </summary>

- Open the `ChromeDriverPlus`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun <T> openWith(url: String, addOption: Set<String>, use: ChromeDriverPlus.() -> T): T </summary>

- Open the `ChromeDriverPlus`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun newChromeDriver(chromeOptions: ChromeOptions): ChromeDriver </summary>

- Open the `ChromeDriver` raw.
- Since `ChromeDriver` does not support auto close unlike `ChromeDriverPlus`, you need to manually release the resources.
</details>

## class ChromeDriverPlus
<details>
<summary style="font-size: 14px; font-weight: bold">var implicitWaitTimeout: Duration</summary>

- same as `ChromeDriver.manage().timeouts().implicitlyWait`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">var pageLoadTimeout: Duration</summary>

- same as `ChromeDriver.manage().timeouts().pageLoadTimeout`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun move(url: String)</summary>

- move to the url
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun scrollDown(size: Int)</summary>

- scroll down by the specified size.
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun scrollDown()</summary>

- scroll down to the bottom of the page
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun script(script: String): Any?</summary>

- same as `JavascriptExecutor.executeScript`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun sleep(millis: Long)</summary>

- same as `Thread.sleep`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun windowSize(width: Int, height: Int)</summary>

- same as `ChromeDriver.manage().window().size`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun find(css: String): WebElement</summary>

- same as `SearchContext.findElement`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun finds(css: String): List<WebElement></summary>

- same as `SearchContext.findElements`
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun findsNotWait(css: String): List<WebElement></summary>

- `finds()` without waiting
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun hasElementsNotWait(css: String): Boolean</summary>

- find exist element without waiting
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun <T> inImplicitWaitTimeout(duration: Duration, run: () -> T): T</summary>

- run in implicit wait timeout
</details>

<details>

<summary style="font-size: 14px; font-weight: bold">fun <T> inPageLoadTimeout(duration: Duration, run: () -> T): T</summary>

- run in page load timeout
</details>


## class ChromeDriverBuilder
- create this object through `ChromeDriverManager.builder()`.
<details>
<summary style="font-size: 14px; font-weight: bold">fun downloadStrategy(downloadStrategy: DownloadStrategy): ChromeDriverBuilder</summary>

- `DownloadStrategy.THROW_IF_NO_VERSION`
    - Throws an error if the version does not exist.
- `DownloadStrategy.DOWNLOAD_IF_NO_VERSION`
    - Downloads if the version does not exist.
- `DownloadStrategy.DOWNLOAD_IF_NO_VERSION_OR_DIFFERENT_REVISION`
    - Downloads if the version does not exist or if the revision is different.

</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun option(option: String): ChromeDriverBuilder</summary>

- Enter the options for `ChromeDriver`.
- However, the `--headless` option cannot be used.
- Instead, use `ChromeDriverManager.openBackground()`.

</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun enableRecommendChromeOptions(disabledSecurity: Boolean): ChromeDriverBuilder</summary>

- recommend chrome options
    ```
    // Prevents socket errors.
    --user-data-dir=System.getProperty("java.io.tmpdir")
    
    // Disables browser information bar.
    --disable-infobars
    
    // Ignores the limit on temporary disk space for the browser.
    --disable-dev-shm-usage
    
    // Disables image loading.
    --blink-settings=imagesEnabled=false
    
    --disable-extensions
    --disable-popup-blocking
    --disable-gpu
    ```
- disabled security options
    ```
    webdriver.chrome.whitelistedIps = "" (system properties)
    --no-sandbox
    --ignore-certificate-errors
    ```
</details>

<details>
<summary style="font-size: 14px; font-weight: bold">fun build(): ChromeDriverManager</summary>

- create `ChromeDriverManager` object
</details>

## Repository
- https://search.maven.org/artifact/me.saro/selenium-chrome-all-in-one
- https://mvnrepository.com/artifact/me.saro/selenium-chrome-all-in-one

## Selenium Project
- https://www.selenium.dev/
