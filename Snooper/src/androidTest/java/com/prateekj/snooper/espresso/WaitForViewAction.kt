package com.prateekj.snooper.espresso

import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import android.view.View

import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher

import androidx.test.espresso.Espresso.registerIdlingResources
import androidx.test.espresso.Espresso.unregisterIdlingResources
import java.lang.String.format

class WaitForViewAction(private val viewMatcher: Matcher<View>, private val timeout: Int) :
  ViewAction {

  override fun getConstraints(): Matcher<View> {
    return anyView()
  }

  override fun getDescription(): String {
    return format("performing wait for until view matcher %s matches", viewMatcher.toString())
  }

  override fun perform(uiController: UiController, view: View) {
    val idlingResource = ViewMatcherIdlingResource(timeout, viewMatcher, view)
    registerIdlingResources(idlingResource)
    uiController.loopMainThreadUntilIdle()
    unregisterIdlingResources(idlingResource)
    if (!idlingResource.isMatched) {
      throw PerformException.Builder()
        .withActionDescription(description)
        .withViewDescription(
          HumanReadables.getViewHierarchyErrorMessage(
            view,
            null,
            "Action timed out : $description",
            null
          )
        )
        .build()
    }
  }

  private fun anyView(): CustomTypeSafeMatcher<View> {
    return object : CustomTypeSafeMatcher<View>("") {
      override fun matchesSafely(item: View): Boolean {
        return true
      }
    }
  }
}
