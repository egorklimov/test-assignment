package org.jetbrains.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.jetbrains.recommender.RandomCoffeeApiClient
import org.jetbrains.recommender.model.SuggestCatForRandomCoffeeRequest
import org.jetbrains.repository.Cat
import org.jetbrains.repository.CatBreedRepository
import org.jetbrains.repository.CatRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class CatWithBreed(val id: Long, val name: String, val breed: String)
data class NewCat(val name: String, val breed: String)

@Service
class CatService(
    private val catRepository: CatRepository,
    private val catBreedRepository: CatBreedRepository,
    private val catRecommenderClient: RandomCoffeeApiClient
) {

    private val log: Logger = LoggerFactory.getLogger(CatService::class.java)

    fun generatePairs(limit: Int): List<Pair<CatWithBreed, CatWithBreed>> {
        val catList = catRepository.findAllWithLimit(limit).map {
            CatWithBreed(
                it.id,
                it.name,
                catBreedRepository.findByIdOrNull(it.breedId)?.name ?: throw RuntimeException("Breed not found")
            )
        }

        return catList.map { cat -> cat to suggestCat(cat) }
            .map { (cat, friendId) -> cat to catRepository.findById(friendId).orElseThrow() }
            .map { (cat, friend) ->
                cat to CatWithBreed(
                    friend.id,
                    friend.name,
                    catBreedRepository.findByIdOrNull(friend.breedId)?.name ?: throw RuntimeException("Breed not found")
                )
            }
    }

    @Transactional
    fun addCat(cat: NewCat): CatWithBreed {
        val breed = catBreedRepository.findByName(cat.breed).orElseThrow { RuntimeException("Breed not found") }
        val createdCat = catRepository.save(Cat(0L, breed.id, cat.name, ""))
        return CatWithBreed(createdCat.id, createdCat.name, breed.name)
    }

    @WithSpan
    private fun suggestCat(cat: CatWithBreed): Long =
        try {
            catRecommenderClient.suggestCat(SuggestCatForRandomCoffeeRequest(cat.id, cat.name, cat.breed)).id
        } catch (e: FeignException) {
            log.error("Failed to suggest cat", e)
            throw CatRecommenderIntegrationException(
                JACKSON_MAPPER.readValue(e.contentUTF8(), FastAPIExceptionResponse::class.java).detail,
                e
            )
        }

    @WithSpan
    @Transactional
    fun findCatsByName(name: String): List<CatWithBreed> {
        val result = catRepository.findAllByName(name).map {
            CatWithBreed(
                it.id,
                it.name,
                catBreedRepository.findByIdOrNull(it.breedId)?.name ?: throw RuntimeException("Breed not found")
            )
        }
        return result
    }

    @WithSpan
    @Transactional
    fun getAllCats(): List<CatWithBreed> =
        catRepository.findAll().map {
            CatWithBreed(
                it.id,
                it.name,
                catBreedRepository.findByIdOrNull(it.breedId)?.name ?: throw RuntimeException("Breed not found")
            )
        }

    @WithSpan
    @Transactional
    fun countCats(): Long = catRepository.countAll()

    companion object {
        private val JACKSON_MAPPER = ObjectMapper()
    }
}

data class FastAPIExceptionResponse(@JsonProperty("detail") val detail: String)
class CatRecommenderIntegrationException(message: String, exception: Exception) : RuntimeException(message, exception)
