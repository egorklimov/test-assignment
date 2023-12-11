package org.jetbrains.recommender.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class SuggestCatForRandomCoffeeRequest(
    @Positive
    @JsonProperty("catId", required = true)
    val catId: Long,
    @NotEmpty
    @JsonProperty("name", required = true)
    val name: String,
    @NotEmpty
    @JsonProperty("breed", required = true)
    val breed: String
)
