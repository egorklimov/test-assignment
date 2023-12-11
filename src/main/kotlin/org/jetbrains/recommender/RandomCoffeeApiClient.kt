package org.jetbrains.recommender

import jakarta.validation.Valid
import org.jetbrains.recommender.model.CatForCoffeeResponse
import org.jetbrains.recommender.model.SuggestCatForRandomCoffeeRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping

@Validated
@FeignClient("randomCoffee")
fun interface RandomCoffeeApiClient {
    @PostMapping("/api/recommend", consumes = ["application/json"])
    fun suggestCat(@Valid suggestCatForRandomCoffeeRequest: SuggestCatForRandomCoffeeRequest): CatForCoffeeResponse
}
