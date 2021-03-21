package com.prateekj.snooper.networksnooper.activity

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.rules.DataResetRule
import com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView
import com.prateekj.snooper.utils.TestUtilities.getDate
import org.hamcrest.core.AllOf.allOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class HttpCallSearchActivityTest {
  @get:Rule
  var dataResetRule = DataResetRule()

  @get:Rule
  var activityRule = IntentsTestRule(HttpCallSearchActivity::class.java, true, false)

  private lateinit var snooperRepo: SnooperRepo

  @Before
  @Throws(Exception::class)
  fun setUp() {
    snooperRepo = SnooperRepo(getInstrumentation().targetContext)
  }

  @Test
  @Throws(Exception::class)
  fun shouldRenderHttpCalls() {
    val beforeDate = getDate(2017, 5, 2, 11, 22, 33)
    val afterDate = getDate(2017, 5, 3, 11, 22, 33)
    saveHttpCall("https://www.google.com", "GET", 200, "OK", beforeDate)
    val facebookCallId = saveHttpCall("https://www.facebook.com", "GET", 200, "OK", afterDate)

    activityRule.launchActivity(null)

    onView(withId(R.id.search_src_text)).perform(typeText("eeeeeee"))
    onView(withText("No results found for 'eeeeeee'")).check(matches(isDisplayed()))

    onView(withId(R.id.search_src_text)).perform(clearText(), typeText(".com"))
    onView(withRecyclerView(R.id.list, 0)).check(
      matches(
        allOf<View>(
          hasDescendant(withText("https://www.facebook.com")),
          hasDescendant(withText("GET")),
          hasDescendant(withText("200")),
          hasDescendant(withText("OK")),
          hasDescendant(withText("06/03/2017 11:22:33"))
        )
      )
    )

    onView(withRecyclerView(R.id.list, 1)).check(
      matches(
        allOf<View>(
          hasDescendant(withText("https://www.google.com")),
          hasDescendant(withText("GET")),
          hasDescendant(withText("200")),
          hasDescendant(withText("OK")),
          hasDescendant(withText("06/02/2017 11:22:33"))
        )
      )
    )

    onView(withId(R.id.search_src_text)).perform(clearText(), typeText("facebook"))
    verifyClickActionOnListItem(0, facebookCallId)

    assertTrue(activityRule.activity.isFinishing)
  }

  private fun verifyClickActionOnListItem(itemIndex: Int, httpCallId: Long) {
    intending(anyIntent()).respondWith(Instrumentation.ActivityResult(RESULT_OK, Intent()))
    onView(withRecyclerView(R.id.list, itemIndex)).perform(click())
    intended(
      allOf(
        hasComponent(HttpCallActivity::class.java.name),
        hasExtra(HTTP_CALL_ID, httpCallId)
      )
    )
  }

  private fun saveHttpCall(
    url: String,
    method: String,
    statusCode: Int,
    statusText: String,
    date: Date
  ): Long {
    val httpCall = HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withStatusCode(statusCode)
      .withStatusText(statusText)
      .build()
    httpCall.date = date
    return snooperRepo.save(HttpCallRecord.from(httpCall))
  }
}