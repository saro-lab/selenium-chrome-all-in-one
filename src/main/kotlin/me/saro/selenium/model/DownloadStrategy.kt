package me.saro.selenium.model

enum class DownloadStrategy {
    // Throws an error if the version does not exist.
    THROW_IF_NO_VERSION,
    // Downloads if the version does not exist.
    DOWNLOAD_IF_NO_VERSION,
    // Downloads if the version does not exist or if the revision is different.
    DOWNLOAD_IF_NO_VERSION_OR_DIFFERENT_REVISION,
}