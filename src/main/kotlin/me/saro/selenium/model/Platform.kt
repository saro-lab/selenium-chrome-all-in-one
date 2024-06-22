package me.saro.selenium.model

enum class Platform(val value: String) {
    LINUX_64("linux64"),
    MAC_ARM64("mac-arm64"),
    MAC_X64("mac-x64"),
    WINDOWS_64("win64");

    companion object {
        @JvmStatic
        fun getPlatform(): Platform {
            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()
            if (!arch.contains("64")) {
                throw RuntimeException("not supported $os / $arch")
            }
            return when {
                os.contains("linux") -> Platform.LINUX_64
                os.contains("windows") -> Platform.WINDOWS_64
                os.contains("mac") -> {
                    if (arch.contains("aarch64")) {
                        Platform.MAC_ARM64
                    } else {
                        Platform.MAC_X64
                    }
                }
                else -> throw RuntimeException("not supported $os / $arch")
            }
        }
    }
}