package cz.schrek.hopper.utils

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

object CoroutineUtils {

    object ProcessIdKey : CoroutineContext.Key<ProcessIdElement>
    class ProcessIdElement(val id: Int) : AbstractCoroutineContextElement(ProcessIdKey)
}


fun CoroutineScope.createHooperContext(id: Int) =
    coroutineContext + CoroutineUtils.ProcessIdElement(id)
