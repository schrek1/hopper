package cz.schrek.hopper.utils

import org.apache.logging.log4j.LogManager

object Logger {

    inline fun Any.getLoggerForClass() = LogManager.getLogger(this::class.java)
}
