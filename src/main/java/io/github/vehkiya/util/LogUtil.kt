package io.github.vehkiya.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


inline fun <reified T> logger(): Log {
    return LogFactory.getLog(T::class.java)
}