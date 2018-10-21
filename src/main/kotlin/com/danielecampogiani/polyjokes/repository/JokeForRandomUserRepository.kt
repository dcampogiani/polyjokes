package com.danielecampogiani.polyjokes.repository

import arrow.Kind
import arrow.typeclasses.MonadError
import com.danielecampogiani.polyjokes.datasource.JokeDataSource
import com.danielecampogiani.polyjokes.datasource.UserDataSource

class JokeForRandomUserRepository<F>(
        AE: MonadError<F, Throwable>,
        private val userDS: UserDataSource<F>,
        private val jokeDS: JokeDataSource<F>
) : MonadError<F, Throwable> by AE {

    fun getRandomJoke(): Kind<F, String> {
        return userDS.randomUser().flatMap {
            jokeDS.getJoke(it)
        }
    }
}