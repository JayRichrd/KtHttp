package com.boycoder.kthttp

import com.boycoder.kthttp.annotations.Field
import com.boycoder.kthttp.annotations.GET
import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`.getRawType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.system.measureTimeMillis

// https://trendings.herokuapp.com/repo?lang=java&since=weekly

//suspend fun <T> KtCall<T>.await(): T =
//    suspendCoroutine { continuation ->
//        call(object : Callback<T> {
//            override fun onSuccess(data: T) {
//                if (data != null) {
//                    continuation.resumeWith(Result.success(data))
//                } else {
//                    continuation.resumeWith(Result.failure(NullPointerException()))
//                }
//            }
//
//            override fun onFail(throwable: Throwable) {
//                continuation.resumeWith(Result.failure(throwable))
//            }
//        })
//    }

//suspend fun <T : Any> KtCall<T>.await(): T =
//    suspendCoroutine { continuation ->
//        call(object : Callback<T> {
//            override fun onSuccess(data: T) {
//                println("Request success!")
//                continuation.resume(data)
//            }
//
//            override fun onFail(throwable: Throwable) {
//                println("Request fail!：$throwable")
//                continuation.resumeWithException(throwable)
//            }
//        })
//    }
val loggerV4: Logger = LogManager.getLogger("KtHttpV4")
suspend fun <T : Any> KtCall<T>.await(): T = suspendCancellableCoroutine { continuation ->
    val call = call(object : Callback<T> {
        override fun onSuccess(data: T) {
            loggerV4.debug("Request success!")
            //println("Request success!")
            continuation.resume(data)
        }

        override fun onFail(throwable: Throwable) {
            loggerV4.error("Request fail!：$throwable")
            continuation.resumeWithException(throwable)
        }
    })

    continuation.invokeOnCancellation {
        loggerV4.debug("Call cancelled!")
        call.cancel()
    }
}

fun main() = runBlocking {
    val start = System.currentTimeMillis()
    val deferred = async {
        KtHttpV3.create(ApiServiceV3::class.java).repos(lang = "Kotlin", since = "weekly").await()
    }

    deferred.invokeOnCompletion {
        loggerV4.error("invokeOnCompletion: error msg: ${it?.localizedMessage}")
    }
    delay(50L)

    //deferred.cancel()
    loggerV4.debug("Time cancel: ${System.currentTimeMillis() - start}")

    try {
        val result = deferred.await()
        loggerV4.debug(result)
    } catch (e: Exception) {
        loggerV4.error("Time exception: ${System.currentTimeMillis() - start}")
        loggerV4.error("Catch exception:$e")
    } finally {
        loggerV4.debug("Time total: ${System.currentTimeMillis() - start}")
    }
}


//fun main() = runBlocking {
//    val ktCall = KtHttpV3.create(ApiServiceV3::class.java)
//        .repos(lang = "Kotlin", since = "weekly")
//
//    val result = ktCall.await()
//    println(result)
//}
