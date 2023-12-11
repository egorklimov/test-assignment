package org.jetbrains.recommender.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CatForCoffeeResponse(
    @JsonProperty("id", required = true)
    val id: Long
)
