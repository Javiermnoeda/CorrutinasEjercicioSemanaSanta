import kotlinx.coroutines.*
import java.lang.Thread.currentThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main(args: Array<String>){
    exampleLaunchCoroutineScope()
}

suspend fun printlnDelayed(message: String){
    delay( 1000)
    println(message)
}

suspend fun calculateHardThings(startNum: Int): Int{
    delay(1000)
    return startNum * 10
}

fun exampleBlocking() = runBlocking{
    println("one")
    printlnDelayed("two") // runBlocking esta bloqueando el hilo y practicamente esta simulanco el thrad.sleep
    println("three")
}

//no se pueden tener todas las funciones como "suspend fun"


// Ejecutando otro hilo pero mientras bloqueamos el hilo main
fun exampleBlockingDispatcher(){
    runBlocking(Dispatchers.Default){
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    // Fuera de runblocking para mostrar que se esta ejecutando en el el hilo principal bloqueado
    println("three - from thread ${Thread.currentThread().name}")
    // Se ejecuta solo depues de que runBlocking este completamente ejecutado
}

fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    delay(3000)
}

fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    job.join()
}

fun exampleLaunchCoroutineScope() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")

    (customDispatcher.executor as ExecutorService).shutdown()
}

fun exampleAsyncAwait() = runBlocking {
    val startTime = System.currentTimeMillis()

    val deferred1 = async { calculateHardThings(startNum = 10) }.await()
    val deferred2 = async { calculateHardThings(startNum = 20) }.await()
    val deferred3 = async { calculateHardThings(startNum = 30) }.await()

    //val sum = deferred1.await() + deferred2.await() + deferred3.await(), esto no funciona no entiendo porque
    //println("async/await result = $sum")
    val endTime = System.currentTimeMillis()
    println("Tiempo que ha tardado: ${endTime - startTime}")
}
