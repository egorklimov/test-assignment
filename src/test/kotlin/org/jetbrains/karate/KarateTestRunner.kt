package org.jetbrains.karate

import com.intuit.karate.junit5.Karate
import org.jetbrains.AbstractIntegrationTest

class KarateTestRunner : AbstractIntegrationTest() {
    @Karate.Test
    fun runAllTestInCurrentPackage(): Karate =
        Karate().relativeTo(this.javaClass)
            .systemProperty("karate.port", localServerPort)
}
