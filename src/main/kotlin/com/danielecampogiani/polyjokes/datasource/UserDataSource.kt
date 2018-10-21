package com.danielecampogiani.polyjokes.datasource

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.typeclasses.Async
import com.danielecampogiani.polyjokes.User
import com.danielecampogiani.polyjokes.api.RandomUserAPI
import com.danielecampogiani.polyjokes.api.UserResponse
import com.danielecampogiani.polyjokes.api.name
import com.danielecampogiani.polyjokes.api.surname
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDataSource<F>(private val A: Async<F>,
                        private val api: RandomUserAPI) {
    fun randomUser(): Kind<F, User> {

        return A.async { callback: (Either<Throwable, User>) -> Unit ->

            api.getUser().enqueue(object : Callback<UserResponse> {

                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        callback(User(body.name, body.surname).right())
                    } else {
                        callback(UnknownException().left())
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    callback(t.left())
                }

            })
        }
    }

    class UnknownException : RuntimeException()

}