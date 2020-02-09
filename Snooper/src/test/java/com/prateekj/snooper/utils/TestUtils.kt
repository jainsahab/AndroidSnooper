package com.prateekj.snooper.utils

import java.util.Calendar
import java.util.Date

object TestUtils {
  fun getDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Date {
    val instance = Calendar.getInstance()
    instance.set(year, month, day, hour, minute, seconds)
    return instance.time
  }
}
