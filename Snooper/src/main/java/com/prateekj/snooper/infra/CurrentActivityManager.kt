package com.prateekj.snooper.infra


import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

import java.util.ArrayList

class CurrentActivityManager private constructor(application: Application) :
  ActivityLifecycleCallbacks {

  private val listeners = ArrayList<Listener>()

  interface Listener {
    fun currentActivity(activity: Activity?)
  }

  init {
    application.registerActivityLifecycleCallbacks(this)
  }

  fun registerListener(listener: Listener) {
    listeners.add(listener)
  }

  fun unregisterListener(listener: Listener) {
    listeners.remove(listener)
  }


  override fun onActivityResumed(activity: Activity?) {
    notifyListeners(activity)
  }


  override fun onActivityPaused(activity: Activity?) {
    notifyListeners(activity)
  }

  private fun notifyListeners(activity: Activity?) {
    for (listener in listeners) {
      listener.currentActivity(activity)
    }
  }

  override fun onActivityStopped(activity: Activity?) {}

  override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

  override fun onActivityStarted(activity: Activity?) {}

  override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

  override fun onActivityDestroyed(activity: Activity?) {}

  companion object {

    private var INSTANCE: CurrentActivityManager? = null

    fun getInstance(application: Application): CurrentActivityManager {
      return INSTANCE ?: CurrentActivityManager(application).also { INSTANCE = it }
    }
  }
}
