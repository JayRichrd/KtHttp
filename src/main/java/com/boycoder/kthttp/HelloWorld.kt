package com.boycoder.kthttp

import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger("HelloWord")
fun main() {
    val result = runBlocking {
        logger.debug("Coroutine started")
        delay(1000L)
        logger.debug("Coroutine end")
        return@runBlocking "runBlocking return"
    }

    val deferred = GlobalScope.async {
        logger.debug("into async")
        delay(1000L)
        logger.debug("out async")
        return@async "async return"
    }
    logger.debug("runBlocking return result = $result")
    logger.debug("async return result = ${deferred}")
    logger.debug("main thread finish")
}