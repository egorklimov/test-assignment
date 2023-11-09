package org.jetbrains

import org.jetbrains.configuration.AppConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration::class)
class CatApplication

val log: Logger = LoggerFactory.getLogger(CatApplication::class.java)
fun main(args: Array<String>) {
    runApplication<CatApplication>(*args)
}
