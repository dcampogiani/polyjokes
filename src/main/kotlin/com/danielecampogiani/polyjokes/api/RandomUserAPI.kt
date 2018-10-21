package com.danielecampogiani.polyjokes.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

interface RandomUserAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit = instance): RandomUserAPI {
            return retrofit.create(RandomUserAPI::class.java)
        }

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl("https://randomuser.me/api/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
        }
    }

    @GET(" ")
    fun getUser(): Call<UserResponse>
}