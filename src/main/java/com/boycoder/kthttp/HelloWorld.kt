package com.boycoder.kthttp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger("HelloWord")
fun main() {
    GlobalScope.launch(Dispatchers.IO) {
        delay(1000L)
        logger.debug("Coroutine started:${Thread.currentThread().name}")
    }

    logger.debug("After launch:${Thread.currentThread().name}")
    Thread.sleep(2000L)
}