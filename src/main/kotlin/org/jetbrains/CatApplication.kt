package org.jetbrains

import org.jetbrains.configuration.AppConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration::class)
class CatApplication

fun main(args: Array<String>) {
    runApplication<CatApplication>(*args)
}
