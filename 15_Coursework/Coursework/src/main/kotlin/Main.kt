import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val storage = Storage()
    launch { storage.unload() }
    launch { storage.load() }
}