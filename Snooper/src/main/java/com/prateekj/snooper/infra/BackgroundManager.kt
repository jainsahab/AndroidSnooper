package com.prateekj.snooper.infra


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler

import com.prateekj.snooper.utils.Logger

import java.util.ArrayList

class BackgroundManager private constructor(application: Application) :
  Application.ActivityLifecycleCallbacks {

  var isInBackground = true
    private set
  private val listeners = ArrayList<Listener>()
  private val mBackgroundDelayHandler = Handler()
  private var mBackgroundTransition: Runnable? = null

  interface Listener {
    fun onBecameForeground()

    fun onBecameBackground()
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

  override fun onActivityResumed(activity: Activity) {
    if (mBackgroundTransition != null) {
      mBackgroundDelayHandler.removeCallbacks(mBackgroundTransition)
      mBackgroundTransition = null
    }

    if (isInBackground) {
      isInBackground = false
      notifyOnBecameForeground()
      Logger.d(TAG, "Application went to foreground")
    }
  }

  private fun notifyOnBecameForeground() {
    for (listener in listeners) {
      try {
        listener.onBecameForeground()
      } catch (e: Exception) {
        Logger.e(TAG, "Listener threw exception!", e)
      }

    }
  }

  override fun onActivityPaused(activity: Activity) {
    if (!isInBackground && mBackgroundTransition == null) {
      mBackgroundTransition = Runnable {
        isInBackground = true
        mBackgroundTransition = null
        notifyOnBecameBackground()
        Logger.d(TAG, "Application went to background")
      }
      mBackgroundDelayHandler.postDelayed(mBackgroundTransition, BACKGROUND_DELAY)
    }
  }

  private fun notifyOnBecameBackground() {
    for (listener in listeners) {
      try {
        listener.onBecameBackground()
      } catch (e: Exception) {
        Logger.e(TAG, "Listener threw exception!", e)
      }

    }
  }

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}

  override fun onActivityStarted(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  override fun onActivityDestroyed(activity: Activity) {}

  companion object {

    private const val BACKGROUND_DELAY: Long = 500
    private val TAG = BackgroundManager::class.java.simpleName

    private var sInstance: BackgroundManager? = null

    fun getInstance(application: Application): BackgroundManager {
      return sInstance ?: BackgroundManager(application)
    }
  }
}
