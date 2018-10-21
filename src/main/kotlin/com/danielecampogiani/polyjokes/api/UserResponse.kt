package com.danielecampogiani.polyjokes.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserResponse(@JsonProperty("results") val results: List<Result>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(@JsonProperty("name") val name: Name)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Name(
        @JsonProperty("first") val first: String,
        @JsonProperty("last") val last: String
)

val UserResponse.name: String
    get() = this.results.firstOrNull()?.name?.first.orEmpty()

val UserResponse.surname: String
    get() = this.results.firstOrNull()?.name?.last.orEmpty()