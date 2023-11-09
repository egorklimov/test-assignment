package org.jetbrains.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppConfiguration(
    val numberOfCatsToAddOnStartup: Long
)
