package com.prateekj.snooper.espresso

import androidx.test.espresso.ViewAction
import android.view.View

import org.hamcrest.Matcher

object EspressoViewActions {
  const val ONE_SECOND = 1000
  fun waitFor(viewMatcher: Matcher<View>): ViewAction {
    return WaitForViewAction(viewMatcher, ONE_SECOND * 5)
  }

  fun waitFor(viewMatcher: Matcher<View>, timeout: Int): ViewAction {
    return WaitForViewAction(viewMatcher, timeout)
  }
}
