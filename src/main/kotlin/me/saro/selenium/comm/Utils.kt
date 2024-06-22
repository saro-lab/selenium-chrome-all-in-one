package me.saro.selenium.comm

import java.io.File
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.reflect.KClass

class Utils {
    companion object {
        fun <T : Any> getLogger(clazz: KClass<T>): Logger = Logger.getLogger(clazz.java.name)

        fun unzip(zipFile: File, zipRootDepth: Int, destDir: File) =
            unzip(zipFile) {
                val paths = it.name.split("/")
                if (paths.size > zipRootDepth) {
                    File(destDir, paths.drop(zipRootDepth).joinToString("/"))
                } else null
            }

        fun unzip(zipFile: File, eachSavePath: (ZipEntry) -> File?) =
            ZipFile(zipFile).use { zip -> zip.entries().asSequence().forEach { entry ->
                eachSavePath(entry)?.also { file ->
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile.mkdirs()
                        file.outputStream().use { output -> zip.getInputStream(entry).copyTo(output) }
                    }
                }
            }}
    }
}
