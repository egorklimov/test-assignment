package org.jetbrains.service

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.jetbrains.repository.CatBreedRepository
import org.jetbrains.repository.CatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class CatWithBreed(val name: String, val breed: String)

@Service
class CatService(
    private val catRepository: CatRepository,
    private val catBreedRepository: CatBreedRepository
) {
    @WithSpan
    @Transactional
    fun getAllCats(): List<CatWithBreed> =
        catRepository.findAll().map {
            CatWithBreed(
                it.name,
                catBreedRepository.findByIdOrNull(it.breedId)?.name ?: throw RuntimeException("Breed not found")
            )
        }

    @WithSpan
    @Transactional
    fun countCats(): Long = catRepository.countAll()
}
