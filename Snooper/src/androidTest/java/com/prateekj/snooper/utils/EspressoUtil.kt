package com.prateekj.snooper.utils


import java.util.Date
import java.util.concurrent.TimeUnit

object EspressoUtil {
  private const val DEFAULT_WAIT_TIME = 1500

  @JvmOverloads
  fun waitFor(timeout: Long = DEFAULT_WAIT_TIME.toLong(), condition: () -> Boolean) {
    Logger.d(EspressoUtil::class.java.simpleName, "Started waiting for condition")
    val endTime = Date().time + timeout

    while (Date().time < endTime) {
      if (condition()) {
        Logger.d(EspressoUtil::class.java.simpleName, "Condition satisfied")
        return
      }
      sleep()
    }

    val message = "Timed out waiting for condition to be satisfied!"
    Logger.e(EspressoUtil::class.java.simpleName, message)
    throw RuntimeException(message)
  }

  private fun sleep() {
    try {
      TimeUnit.SECONDS.sleep(1)
    } catch (e: InterruptedException) {
      e.printStackTrace()
    }

  }
}
