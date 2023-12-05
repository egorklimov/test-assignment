package org.jetbrains.service

import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jetbrains.configuration.AppConfiguration
import org.jetbrains.repository.Cat
import org.jetbrains.repository.CatBreedRepository
import org.jetbrains.repository.CatRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CatInitializer(
    val catBreedRepository: CatBreedRepository,
    val catRepository: CatRepository,
    val appConfiguration: AppConfiguration
) {
    private val log: Logger = LoggerFactory.getLogger(CatInitializer::class.java)

    @WithSpan
    @EventListener(ApplicationReadyEvent::class)
    fun createCats() = runBlocking {
        val catBreedIds: List<Long> = catBreedRepository.findAll().map { it.id }.toList()
        generateSequence { Cat(0L, catBreedIds.random(), CAT_NAMES.random(), CAT_SPOTS.random()) }
            .take(appConfiguration.numberOfCatsToAddOnStartup)
            .chunked(appConfiguration.batchSize)
            .map { GlobalScope.async { createCatBatch(it) } }
            .toList()
            .awaitAll()
        log.info("Added {} cats to the database", appConfiguration.numberOfCatsToAddOnStartup)
    }

    @Transactional
    fun createCatBatch(batch: List<Cat>) {
        catRepository.saveAll(batch)
    }

    companion object {
        private val CAT_SPOTS: Set<String> = setOf(
            "Ears",
            "Chin",
            "Cheeks",
            "Back",
            "Head",
            "Belly"
        )
        private val CAT_NAMES: Set<String> = setOf(
            "Luna",
            "Bella",
            "Lucy",
            "Sophie",
            "Stella",
            "Lily",
            "Lola",
            "Ginger",
            "Kitty",
            "Oreo",
            "Olive",
            "Molly",
            "Frankie",
            "Cleo",
            "Willow",
            "Chloe",
            "Penny",
            "Millie",
            "Mia",
            "Coco",
            "Blue",
            "Ziggy",
            "Mouse",
            "Lulu",
            "Lilly",
            "Gracie",
            "Callie",
            "Violet",
            "Rosie",
            "Kiki",
            "Izzy",
            "Daisy",
            "Zoe",
            "Peaches",
            "Angel",
            "Ruby",
            "Phoebe",
            "Maggie",
            "Ivy",
            "Gigi",
            "Ellie",
            "Dusty",
            "Bonnie",
            "Trixie",
            "Sadie",
            "Poppy",
            "Pearl",
            "Olivia",
            "Maddie",
            "Lizzie"
        )
    }
}
