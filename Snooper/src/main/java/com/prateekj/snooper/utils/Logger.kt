package com.prateekj.snooper.utils

import android.util.Log

object Logger {
  private const val SNOOPER_DEBUGGER_TAG = "AndroidSnooper"

  fun d(tag: String, message: String?) {
    Log.d(SNOOPER_DEBUGGER_TAG + tag, message)
  }

  fun e(tag: String, message: String?, exception: Throwable) {
    Log.e(SNOOPER_DEBUGGER_TAG + tag, message, exception)
  }

  fun e(tag: String, message: String?) {
    Log.e(SNOOPER_DEBUGGER_TAG + tag, message)
  }
}
