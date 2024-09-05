package cz.schrek.hopper.utils

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

object CoroutineUtils {

    object ProcessIdKey : CoroutineContext.Key<ProcessIdElement>
    class ProcessIdElement(val id: Int) : AbstractCoroutineContextElement(ProcessIdKey)
}
