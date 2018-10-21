# An example of a polymorphic approach using Arrow

Quoting excellent [Arrow documentation](https://arrow-kt.io/docs/patterns/polymorphic_programs/)

> What if we could write apps without caring about the runtime data types used but just about how the data is operated on? 

> Let’s say we have an application working with RxJava’s Observable. We’ll end up having a bunch of chained call stacks based on that given data type. But at the end of the day, and for the sake of simplicity, wouldn’t Observable be just like a “container” with some extra powers?

> And same story for other “containers” like Flowable, Deferred (coroutines), Future, IO, and many more.

> Conceptually, all those types represent an operation (already done or pending to be done), which could support things like mapping over the inner value, flatMapping to chain other operations of the same type, zipping it with other instances of the same type, and so on.

> What if we could write our programs just based on those behaviours in such a declarative style? We could make them be agnostic from concrete data types like Observable. We’d just need to be sure that the data types support a certain contract, so they are “mappable”, “flatMappable”, and so on.

> This approach could sound a bit weird or smell to overengineering, but it has some interesting benefits. Let’s put our eyes on a simple example first and then we talk about those. Deal?

In this example, we are going to use [RandomUser Api](https://randomuser.me/) and [The Internet Chuck Norris Database](http://www.icndb.com/api/) to retrieve a random user and then a joke about him.

## UserDataSource

To obtain a random user we will use UserDataSource<F>:

```kotlin
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
```

```<F>``` will be IO, Single, Observable etc, but in this class, we don't care. We use retrofit to make a network call, and return an instance of ```Kind<F, User>``` thanks to [```Async<F>```](https://arrow-kt.io/docs/effects/async/).

## JokeDataSource

Give and user, with JokeDataSource<F> we can retrieve a Joke about him. The code is similar to UserDataSource<F>:
  
```kotlin
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
```

Once again here we don't care about the concrete ```<F>```, and we are using [```Async<F>```](https://arrow-kt.io/docs/effects/async/) to return an instance of ```Kind<F, String>```.

## JokeForRandomUserRepository

Now that we have the data sources we can combine them inside JokeForRandomUserRepository<F>:
  
```kotlin
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
```
Implementation is pretty simple, we just need to obtain an instance of ```Kind<F, User>```, and then we can use flatmap to concatenate the following operation.

## Module

Module<F> is used to connect the dots. The only thing we need from *outside* is an instance of ```Async<F>```, once we get it we can create the data sources and the repository:
  
```kotlin
class Module<F>(A: Async<F>, userAPI: RandomUserAPI = RandomUserAPI(), jokesAPI: JokesAPI = JokesAPI()) {
    private val userDS: UserDataSource<F> = UserDataSource(A, userAPI)
    private val jokeDS: JokeDataSource<F> = JokeDataSource(A, jokesAPI)
    val repository: JokeForRandomUserRepository<F> =
            JokeForRandomUserRepository(A, userDS, jokeDS)
}
```

## Usage

Now we just need to create our module, we can instantiate it with different async, such as:
 - Module(IO.async())
 - Module(SingleK.async())
 - Module(MaybeK.async())
 - Module(ObservableK.async())
 - Module(FlowableK.async())
 - Module(DeferredK.async())
 
Once the module is created we just use the repository:

```kotlin
Module(IO.async()).run {
        println("IO: ${repository.getRandomJoke().fix().attempt().unsafeRunSync()}")
    }
 ```
 
 or
 
 ```kotlin
Module(DeferredK.async()).run {
        println("Deferred: ${repository.getRandomJoke().fix().unsafeAttemptSync()}")
    }
 ```
For all possible usages you can read [Main.kt](https://github.com/dcampogiani/polyjokes/blob/master/src/main/kotlin/com/danielecampogiani/polyjokes/Main.kt)
