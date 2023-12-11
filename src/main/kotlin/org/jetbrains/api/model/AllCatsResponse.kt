package org.jetbrains.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid

data class AllCatsResponse(
    @JsonProperty("total", required = true)
    val total: Long,
    @Valid
    @JsonProperty("cats", required = true)
    val cats: List<CatResponse>
)
