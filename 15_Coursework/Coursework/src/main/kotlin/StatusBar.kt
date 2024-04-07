import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

object StatusBar {
    val print = flow {
        while (currentCoroutineContext().isActive) {
            delay(750)
            emit('|')
        }
    }
}