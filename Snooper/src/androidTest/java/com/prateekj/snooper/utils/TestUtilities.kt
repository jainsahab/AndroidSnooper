package com.prateekj.snooper.utils

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Calendar
import java.util.Date

object TestUtilities {

  @Throws(IOException::class)
  fun readFileAsStream(assetFileName: String): InputStream {
    return getInstrumentation().context.assets.open(assetFileName)
  }

  @Throws(IOException::class)
  fun readFrom(assetFileName: String): String {
    val inputStream = readFileAsStream(assetFileName)
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    return bufferedReader.use { it.readText() }
  }

  fun getDate(year: Int, month: Int, day: Int): Date {
    val instance = Calendar.getInstance()
    instance.set(year, month, day)
    return instance.time
  }

  fun getDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Date {
    val instance = Calendar.getInstance()
    instance.set(year, month, day, hour, minute, seconds)
    return instance.time
  }

  fun getCalendar(date: Date): Calendar {
    val instance = Calendar.getInstance()
    instance.time = date
    return instance
  }
}
