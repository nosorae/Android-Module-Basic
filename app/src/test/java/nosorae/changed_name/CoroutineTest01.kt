package nosorae.changed_name

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineTest01 {
    @Test
    fun test01() = runBlocking {
        val time = measureTimeMillis {
            val first = getFirst()
            val last = getLast()
            println("Hello $first $last")
        }
        println(time)
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val first = async { getFirst() }
            val last = async { getLast() }
            println("Hello ${first.await()} ${last.await()}")
        }
        println(time)
    }

    suspend fun getFirst() : String {
        delay(1000)
        return "노"
    }
    suspend fun getLast(): String {
        delay(1000)
        return "소래"
    }

}