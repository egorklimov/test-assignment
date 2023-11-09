package org.jetbrains

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected lateinit var localServerPort: String

    companion object {
        @JvmStatic
        @Container
        private val PG: PostgreSQLContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:15.2-alpine"))

        @JvmStatic
        @DynamicPropertySource
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { PG.jdbcUrl }
            registry.add("spring.datasource.username") { PG.username }
            registry.add("spring.datasource.password") { PG.password }
            registry.add("app.number-of-cats-to-add-on-startup") { "10" }
        }
    }
}
