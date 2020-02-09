package com.prateekj.snooper

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class SnooperInstrumentationRunner : AndroidJUnitRunner() {

  var application: Application? = null

  @Throws(
    InstantiationException::class,
    IllegalAccessException::class,
    ClassNotFoundException::class
  )
  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, TestApplication::class.java.name, context)
  }

  override fun callApplicationOnCreate(app: Application) {
    this.application = app
    super.callApplicationOnCreate(app)
  }
}
