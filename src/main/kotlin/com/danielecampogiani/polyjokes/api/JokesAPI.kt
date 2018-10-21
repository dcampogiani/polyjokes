package com.danielecampogiani.polyjokes.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface JokesAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit = instance): JokesAPI {
            return retrofit.create(JokesAPI::class.java)
        }

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl("http://api.icndb.com/jokes/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
        }
    }

    @GET("random")
    fun getJoke(@Query("firstName") firstName: String, @Query("lastName") lastName: String): Call<JokeResponse>
}