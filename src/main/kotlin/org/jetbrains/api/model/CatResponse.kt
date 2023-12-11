package org.jetbrains.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class CatResponse(
    @Positive
    @JsonProperty("id", required = true)
    val id: Long,
    @NotEmpty
    @JsonProperty("name", required = true)
    val name: String,
    @NotEmpty
    @JsonProperty("breed", required = true)
    val breed: String
)
