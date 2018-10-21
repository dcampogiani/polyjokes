package com.danielecampogiani.polyjokes


import arrow.effects.*

fun main(args: Array<String>) {

    Module(IO.async()).run {
        println("IO: ${repository.getRandomJoke().fix().attempt().unsafeRunSync()}")
    }

    Module(SingleK.async()).run {
        repository.getRandomJoke().fix().single.subscribe({ println("Single: $it") }, { println("Single: $it") })
    }

    Module(MaybeK.async()).run {
        repository.getRandomJoke().fix().maybe.subscribe({ println("Maybe: $it") }, { println("Maybe: $it") })
    }

    Module(ObservableK.async()).run {
        repository.getRandomJoke().fix().observable.subscribe({ println("Observable: $it") }, { println("Observable: $it") })
    }

    Module(FlowableK.async()).run {
        repository.getRandomJoke().fix().flowable.subscribe({ println("Flowable: $it") }, { println("Flowable: $it") })
    }

    Module(DeferredK.async()).run {
        println("Deferred: ${repository.getRandomJoke().fix().unsafeAttemptSync()}")
    }
}
