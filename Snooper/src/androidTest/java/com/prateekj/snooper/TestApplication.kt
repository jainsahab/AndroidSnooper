package com.prateekj.snooper

import android.app.Application

class TestApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    AndroidSnooper.init(this)
  }
}
