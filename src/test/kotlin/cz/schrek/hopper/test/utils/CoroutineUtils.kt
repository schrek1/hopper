package cz.schrek.hopper.test.utils

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object CoroutineUtils {

    fun runTest(block: suspend () -> Unit) = runBlocking {
        withTimeout(timeout = 2.minutes) { block() }
    }
}
