package com.prateekj.snooper

import android.app.Application

import com.prateekj.snooper.networksnooper.model.HttpCall

import java.io.IOException

class AndroidSnooper private constructor() {

  @Throws(IOException::class)
  fun record(httpCall: HttpCall) {
  }

  companion object {
    @Volatile
    private var INSTANCE: AndroidSnooper? = null

    fun init(application: Application): AndroidSnooper {
      return INSTANCE ?: AndroidSnooper().also {
        INSTANCE = it
      }
    }

    val instance: AndroidSnooper
      get() {
        if (INSTANCE == null) {
          throw RuntimeException("Android Snooper is not initialized yet")
        }
        return INSTANCE!!
      }
  }
}
