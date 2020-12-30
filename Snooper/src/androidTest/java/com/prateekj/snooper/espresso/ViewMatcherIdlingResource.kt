package com.prateekj.snooper.espresso

import androidx.test.espresso.IdlingResource
import android.view.View

import com.prateekj.snooper.utils.Logger

import org.hamcrest.Matcher

import java.lang.Thread.sleep

class ViewMatcherIdlingResource(
  private val waitTimeInMillis: Int,
  private val viewMatcher: Matcher<in View>,
  private val view: View
) : IdlingResource {
  private var resourceCallback: IdlingResource.ResourceCallback? = null
  private var startTime: Long = 0
  var isMatched: Boolean = false
    private set

  init {
    isMatched = false
  }

  override fun getName(): String {
    return this.javaClass.name
  }

  override fun isIdleNow(): Boolean {
    val timeExceeded = now() - startTime >= waitTimeInMillis
    if (isMatched || timeExceeded) {
      resourceCallback!!.onTransitionToIdle()
    }
    return isMatched || timeExceeded
  }

  override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
    startTime = now()
    this.resourceCallback = resourceCallback
    pollViewUntilMatchesOrTimeout(resourceCallback)
  }

  private fun pollViewUntilMatchesOrTimeout(resourceCallback: IdlingResource.ResourceCallback) {
    val thread = Thread {
      while (!viewMatcher.matches(view) && now() - startTime < waitTimeInMillis) {
        try {
          Logger.d(TAG, "polling view to match $viewMatcher")
          sleep(MATCHER_POLLING_INTERVAL_IN_MILLIS.toLong())
        } catch (ignored: InterruptedException) {
        }

      }
      isMatched = viewMatcher.matches(view)
      resourceCallback.onTransitionToIdle()
    }
    thread.start()
  }

  private fun now(): Long {
    return System.currentTimeMillis()
  }

  companion object {

    private const val MATCHER_POLLING_INTERVAL_IN_MILLIS = 100
    val TAG: String = ViewMatcherIdlingResource::class.java.simpleName
  }
}
