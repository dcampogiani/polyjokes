package com.danielecampogiani.polyjokes

import arrow.effects.typeclasses.Async
import com.danielecampogiani.polyjokes.api.JokesAPI
import com.danielecampogiani.polyjokes.api.RandomUserAPI
import com.danielecampogiani.polyjokes.datasource.JokeDataSource
import com.danielecampogiani.polyjokes.datasource.UserDataSource
import com.danielecampogiani.polyjokes.repository.JokeForRandomUserRepository

class Module<F>(A: Async<F>, userAPI: RandomUserAPI = RandomUserAPI(), jokesAPI: JokesAPI = JokesAPI()) {
    private val userDS: UserDataSource<F> = UserDataSource(A, userAPI)
    private val jokeDS: JokeDataSource<F> = JokeDataSource(A, jokesAPI)
    val repository: JokeForRandomUserRepository<F> =
            JokeForRandomUserRepository(A, userDS, jokeDS)
}