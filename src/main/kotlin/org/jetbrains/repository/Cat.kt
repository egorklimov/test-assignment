package org.jetbrains.repository

import org.jetbrains.configuration.AppConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Table("cat")
data class Cat(
    @Id
    var id: Long,
    @Column("breed_id")
    val breedId: Long,
    val name: String,
    @Column("favorite_spot")
    val favoriteSpot: String
)

interface CatRepository : CrudRepository<Cat, Long> {
    @Query("SELECT COUNT(id) FROM CAT")
    fun countAll(): Long
}

@Component
class CatInitializer(
    val catBreedRepository: CatBreedRepository,
    val catRepository: CatRepository,
    val appConfiguration: AppConfiguration
) {

    @Transactional
    @EventListener(ApplicationReadyEvent::class)
    fun createCats() {
        val catBreedIds: List<Long> = catBreedRepository.findAll().map { it.id }.toList()
        generateSequence { Cat(0L, catBreedIds.random(), CAT_NAMES.random(), CAT_SPOTS.random()) }
            .take(appConfiguration.numberOfCatsToAddOnStartup.toInt())
            .chunked(100)
            .forEach { createCatBatch(it) }
    }

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
