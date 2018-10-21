package com.danielecampogiani.polyjokes.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JokeResponse(@JsonProperty("value") val value: Value)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Value(@JsonProperty("joke") val joke: String)