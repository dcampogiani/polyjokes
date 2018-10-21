package com.danielecampogiani.polyjokes.datasource

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.typeclasses.Async
import com.danielecampogiani.polyjokes.User
import com.danielecampogiani.polyjokes.api.JokeResponse
import com.danielecampogiani.polyjokes.api.JokesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JokeDataSource<F>(private val A: Async<F>,
                        private val api: JokesAPI) {
    fun getJoke(user: User): Kind<F, String> {

        return A.async { callback: (Either<Throwable, String>) -> Unit ->

            api.getJoke(user.name, user.surname).enqueue(object : Callback<JokeResponse> {

                override fun onResponse(call: Call<JokeResponse>, response: Response<JokeResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        callback(body.value.joke.right())
                    } else {
                        callback(UnknownException().left())
                    }
                }

                override fun onFailure(call: Call<JokeResponse>, t: Throwable) {
                    callback(t.left())
                }

            })
        }
    }

    class UnknownException : RuntimeException()
}

