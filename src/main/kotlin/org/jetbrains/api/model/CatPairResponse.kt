package org.jetbrains.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid

data class CatPairResponse(
    @Valid
    @JsonProperty("first", required = true)
    val first: CatResponse,
    @Valid
    @JsonProperty("second", required = true)
    val second: CatResponse
)
