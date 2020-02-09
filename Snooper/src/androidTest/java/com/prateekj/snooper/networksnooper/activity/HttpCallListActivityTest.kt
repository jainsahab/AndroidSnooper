package com.prateekj.snooper.networksnooper.activity

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
import androidx.test.runner.AndroidJUnit4
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.rules.DataResetRule
import com.prateekj.snooper.utils.EspressoViewMatchers.withListSize
import com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView
import com.prateekj.snooper.utils.TestUtilities.getDate
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class HttpCallListActivityTest {

  @get:Rule
  val dataResetRule = DataResetRule()

  @get:Rule
  val activityRule = IntentsTestRule(HttpCallListActivity::class.java, true, false)

  private lateinit var snooperRepo: SnooperRepo

  @Before
  @Throws(Exception::class)
  fun setUp() {
    snooperRepo = SnooperRepo(getTargetContext())
  }

  @Test
  @Throws(Exception::class)
  fun shouldRenderHttpCalls() {
    val beforeDate = getDate(2017, 5, 2, 11, 22, 33)
    val afterDate = getDate(2017, 5, 3, 11, 22, 33)
    val googleCallId = saveHttpCall("https://www.google.com", "GET", 200, "OK", beforeDate)
    val facebookCallId = saveHttpCall("https://www.facebook.com", "GET", 200, "OK", afterDate)

    activityRule.launchActivity(null)

    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()))
    onView(withText(R.string.done)).check(matches(isDisplayed()))
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

    verifyClickActionOnListItem(0, facebookCallId)
    verifyClickActionOnListItem(1, googleCallId)

    onView(withText(R.string.done)).perform(click())
    assertTrue(activityRule.activity.isFinishing)
  }

  @Test
  @Throws(Exception::class)
  fun shouldRenderWithoutHttpCalls() {
    activityRule.launchActivity(null)

    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()))
    onView(withText(R.string.done)).check(matches(isDisplayed()))
    onView(withText(R.string.no_calls_found)).check(matches(isDisplayed()))
    onView(withId(R.id.delete_records_menu)).check(doesNotExist())

    onView(withText(R.string.done)).perform(click())
    assertTrue(activityRule.activity.isFinishing)
  }

  @Test
  @Throws(Exception::class)
  fun shouldVerifyDeleteBehaviorWhenDeleteTapped() {
    val currentDateMillis = getDate(2017, 4, 1, 11, 22, 33).time
    for (index in 0..49) {
      saveHttpCall(
        "https://www.facebook.com/$index",
        "GET",
        200,
        "OK",
        Date(currentDateMillis + index * 1000)
      )
    }
    saveErrorHttpCall("https://www.abc.com", "GET", getDate(2017, 5, 1, 11, 22, 33))
    saveHttpCall("https://www.google.com", "GET", 200, "OK", getDate(2017, 5, 2, 11, 22, 33))
    saveHttpCall("https://www.facebook.com", "GET", 200, "OK", getDate(2017, 5, 3, 11, 22, 33))

    activityRule.launchActivity(null)
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

    onView(withRecyclerView(R.id.list, 2)).check(
      matches(
        allOf<View>(
          hasDescendant(withText("https://www.abc.com")),
          hasDescendant(withText("FAILED")),
          hasDescendant(withText("06/01/2017 11:22:33"))
        )
      )
    )

    onView(withId(R.id.delete_records_menu)).perform(click())
    onView(withText(R.string.delete_records_dialog_cancellation)).perform(click())
    onView(withId(R.string.delete_records_dialog_text)).check(doesNotExist())

    onView(withId(R.id.delete_records_menu)).perform(click())
    onView(withText(R.string.delete_records_dialog_confirmation)).perform(click())
    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()))
    onView(withId(R.id.list)).check(matches(withListSize(0)))
  }

  private fun verifyClickActionOnListItem(itemIndex: Int, httpCallId: Long) {
    intending(anyIntent()).respondWith(Instrumentation.ActivityResult(RESULT_OK, Intent()))
    onView(withRecyclerView(R.id.list, itemIndex)).perform(click())
    intended(
      allOf(
        hasComponent(HttpCallActivity::class.java.name),
        hasExtra(HttpCallActivity.HTTP_CALL_ID, httpCallId)
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

  private fun saveErrorHttpCall(url: String, method: String, date: Date) {
    val error =
      "java.net.ConnectException: failed to connect to localhost/127.0.0.1 " + "(port 80) after 30000ms: isConnected failed: ECONNREFUSED (Connection refused)"
    val httpCall = HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withError(error)
      .build()
    httpCall.date = date
    snooperRepo!!.save(HttpCallRecord.from(httpCall))
  }
}
