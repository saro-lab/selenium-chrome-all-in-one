import java.net.HttpURLConnection
import java.net.URI
import java.util.*

plugins {
    val kotlinVersion = "2.2.20"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    signing
    `maven-publish`
    java
    idea
}

val appGroupId = "me.saro"
val appProps = Properties().apply { file("src/main/resources/application.properties").inputStream().use { load(it) } }
val seleniumVersion = appProps["selenium.version"]
val allInOneVersion = "$seleniumVersion.${appProps["selenium.caio.version"]}"
val chromeVersion = appProps["chrome.version"]

println("seleniumVersion: $seleniumVersion")
println("allInOneVersion: $allInOneVersion")
println("chromeVersion: $chromeVersion")

repositories {
    mavenCentral()
}

idea {
    module {
        excludeDirs = listOf("build", "logs", "tmp").map { file(it) }.toSet()
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // selenium
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    api("org.seleniumhq.selenium:selenium-api:$seleniumVersion")
    api("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion")
    api("org.seleniumhq.selenium:selenium-devtools-v$chromeVersion:$seleniumVersion")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")

    // saro kit
    implementation("me.saro:kit:0.2.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            groupId = appGroupId
            artifactId = "selenium-chrome-all-in-one"
            version = allInOneVersion

            from(components["java"])

            repositories {
                maven {
                    credentials {
                        try {
                            username = project.property("sonatype.username").toString()
                            password = project.property("sonatype.password").toString()
                        } catch (e: Exception) {
                            println("warn: " + e.message)
                        }
                    }
                    name = "ossrh-staging-api"
                    url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                }
            }

            pom {
                name.set("Selenium All-in-One")
                description.set("Selenium All-in-One")
                url.set("https://saro.me")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("PARK Yong Seo")
                        email.set("j@saro.me")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/saro-lab/selenium-all-in-one.git")
                    developerConnection.set("scm:git:git@github.com:saro-lab/selenium-all-in-one.git")
                    url.set("https://github.com/saro-lab/selenium-all-in-one")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.named("publish").configure {
    doLast {
        val username = project.property("sonatype.username").toString()
        val password = project.property("sonatype.password").toString()
        val connection = URI.create("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$appGroupId").toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray()))
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.write("""{"publishing_type": "automatic"}""".toByteArray())
        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            println("Successfully uploaded to Central Portal")
        } else {
            throw GradleException("Failed to upload to Central Portal: $responseCode - ${connection.inputStream?.bufferedReader()?.readText()}")
        }
    }
}

tasks.withType<Javadoc>().configureEach {
    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("Xdoclint:none", true)
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    useJUnitPlatform()
}
