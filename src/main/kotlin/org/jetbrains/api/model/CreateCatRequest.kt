package org.jetbrains.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class CreateCatRequest(
    @NotEmpty
    @JsonProperty("name", required = true)
    val name: String,
    @NotEmpty
    @JsonProperty("breed", required = true)
    val breed: String
)
