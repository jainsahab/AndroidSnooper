package com.prateekj.snooper.networksnooper.activity

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_SEND
import android.view.View
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel
import com.prateekj.snooper.rules.DataResetRule
import com.prateekj.snooper.rules.RunUsingLooper
import com.prateekj.snooper.utils.EspressoIntentMatchers
import com.prateekj.snooper.utils.EspressoViewMatchers.hasBackgroundSpanOn
import com.prateekj.snooper.utils.TestUtilities.getDate
import com.prateekj.snooper.utils.TestUtilities.readFrom
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HttpCallActivityTest {

  @get:Rule
  var grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(WRITE_EXTERNAL_STORAGE)

  @get:Rule
  var rule = DataResetRule()

  @get:Rule
  var runUsingLooper = RunUsingLooper()

  @get:Rule
  var activityRule = IntentsTestRule(HttpCallActivity::class.java, true, false)

  private var snooperRepo: SnooperRepo? = null

  private val responseHeaders = mapOf(
    "content-type" to listOf("application/json"),
    "cache-control" to listOf("no-cache"),
    "content-disposition" to listOf("attachment"),
    "date" to listOf("Sun, 02 Apr 2017 08:54:39 GMT")
  )

  private val requestHeaders = mapOf(
    "content-type" to listOf("application/json"),
    "content-length" to listOf("403"),
    "accept-language" to listOf("en-US,en", "q=0.8,hi", "q=0.6"),
    ":scheme" to listOf("https")
  )

  @Before
  @Throws(Exception::class)
  fun setUp() {
    snooperRepo = SnooperRepo(getTargetContext())
  }

  @Test
  @Throws(Exception::class)
  fun shouldRenderRequestAndResponseBody() {
    val responseBody = readFrom("person_details_raw_response.json")
    val requestPayload = readFrom("person_details_raw_request.json")
    val callId = saveHttpCall(
      "https://www.abc.com/person/1",
      "GET",
      200,
      "OK",
      responseBody,
      requestPayload
    )

    val intent = Intent()
    intent.putExtra(HTTP_CALL_ID, callId)

    activityRule.launchActivity(intent)

    onView(withText(R.string.title_http_call_activity)).check(matches(isDisplayed()))
    onView(allOf<View>(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_response.json"))))
    onView(withId(R.id.copy_menu)).perform(click())
    assertThat(
      clipBoardText(),
      `is`<String>(readFrom("person_details_formatted_response.json"))
    )
    onView(withId(R.id.search_menu)).perform(click())
    onView(withId(R.id.search_src_text)).perform(typeText("streetaddress"))
    onView(allOf<View>(withId(R.id.payload_text), isDisplayed()))
      .check(
        matches(
          hasBackgroundSpanOn(
            "streetAddress",
            R.color.snooper_text_highlight_color
          )
        )
      )

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click())
    onView(allOf<View>(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))))
    onView(withId(R.id.copy_menu)).perform(click())
    assertThat(clipBoardText(), `is`<String>(readFrom("person_details_formatted_request.json")))
    onView(withId(R.id.search_menu)).perform(click())
    onView(withId(R.id.search_src_text)).perform(typeText("delhi"))
    onView(allOf<View>(withId(R.id.payload_text), isDisplayed()))
      .check(matches(hasBackgroundSpanOn("Delhi", R.color.snooper_text_highlight_color)))

    onView(withText("HEADERS")).check(matches(isDisplayed())).perform(click())

    onView(withText("https://www.abc.com/person/1")).check(matches(isDisplayed()))
    onView(withText("GET")).check(matches(isDisplayed()))
    onView(withText("200")).check(matches(isDisplayed()))
    onView(withText("OK")).check(matches(isDisplayed()))
    onView(withText("06/02/2017 11:22:33")).check(matches(isDisplayed()))

    onView(withText(R.string.response_headers)).perform(click())
    verifyResponseHeader("content-type", "application/json")
    verifyResponseHeader("cache-control", "no-cache")
    verifyResponseHeader("content-disposition", "attachment")
    verifyResponseHeader("date", "Sun, 02 Apr 2017 08:54:39 GMT")

    onView(withText(R.string.request_headers)).perform(click())
    verifyRequestHeader("content-type", "application/json")
    verifyRequestHeader("content-length", "403")
    verifyRequestHeader("accept-language", "en-US,en;q=0.8,hi;q=0.6")
    verifyRequestHeader(":scheme", "https")

    intending(anyIntent()).respondWith(Instrumentation.ActivityResult(RESULT_OK, Intent()))
    onView(withId(R.id.share_menu)).perform(click())
    verifyClickActionOnShareMenu()
  }

  @Test
  @Throws(Exception::class)
  fun shouldRenderRequestAndErrorBody() {
    val requestPayload = readFrom("person_details_raw_request.json")
    val error =
      "java.net.ConnectException: failed to connect to localhost/127.0.0.1 " + "(port 80) after 30000ms: isConnected failed: ECONNREFUSED (Connection refused)"
    val callId =
      saveHttpCallWithError("https://www.abc.com/person/1", "GET", error, requestPayload)

    val intent = Intent()
    intent.putExtra(HTTP_CALL_ID, callId)

    activityRule.launchActivity(intent)

    onView(withText("ERROR")).check(matches(isDisplayed()))
    onView(withText(error)).check(matches(isDisplayed()))
    onView(withId(R.id.copy_menu)).perform(click())
    assertThat(clipBoardText(), `is`(error))

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click())
    onView(allOf<View>(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))))
    onView(withId(R.id.copy_menu)).perform(click())
    assertThat(clipBoardText(), `is`<String>(readFrom("person_details_formatted_request.json")))

    onView(withText("HEADERS")).check(matches(isDisplayed())).perform(click())

    onView(withText("https://www.abc.com/person/1")).check(matches(isDisplayed()))
    onView(withText("FAILED")).check(matches(isDisplayed()))
    onView(withText("06/02/2017 11:22:33")).check(matches(isDisplayed()))

    onView(withText(R.string.request_headers)).perform(click())
    verifyRequestHeader("content-type", "application/json")
    verifyRequestHeader("content-length", "403")
    verifyRequestHeader("accept-language", "en-US,en;q=0.8,hi;q=0.6")
    verifyRequestHeader(":scheme", "https")
  }

  private fun verifyResponseHeader(headerName: String, headerValue: String) {
    onData(
      withHeaderData(
        headerName,
        headerValue
      )
    ).inAdapterView(withId(R.id.response_header_list)).check(
      matches(
        allOf<View>(
          hasDescendant(withText(headerName)),
          hasDescendant(withText(headerValue))
        )
      )
    )
  }

  private fun verifyRequestHeader(headerName: String, headerValue: String) {
    onData(
      withHeaderData(
        headerName,
        headerValue
      )
    ).inAdapterView(withId(R.id.request_header_list)).check(
      matches(
        allOf<View>(
          hasDescendant(withText(headerName)),
          hasDescendant(withText(headerValue))
        )
      )
    )
  }

  private fun withHeaderData(
    headerName: String,
    headerValue: String
  ): Matcher<HttpHeaderViewModel> {
    return object : CustomTypeSafeMatcher<HttpHeaderViewModel>("Header with") {
      override fun matchesSafely(viewModel: HttpHeaderViewModel): Boolean {
        return viewModel.headerName() == headerName && viewModel.headerValues() == headerValue
      }
    }
  }

  private fun clipBoardText(): String {
    val clipboard = getTargetContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val lastItemIndex = clipboard.primaryClip!!.itemCount - 1
    return clipboard.primaryClip!!.getItemAt(lastItemIndex).text.toString()
  }

  private fun saveHttpCall(
    url: String, method: String, statusCode: Int,
    statusText: String, responseBody: String, requestPayload: String
  ): Long {
    val requestHeaders = requestHeaders
    val responseHeaders = responseHeaders
    val httpCall = HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withStatusCode(statusCode)
      .withStatusText(statusText)
      .withResponseBody(responseBody)
      .withPayload(requestPayload)
      .withRequestHeaders(requestHeaders)
      .withResponseHeaders(responseHeaders)
      .build()
    httpCall.date = getDate(2017, 5, 2, 11, 22, 33)
    return snooperRepo!!.save(HttpCallRecord.from(httpCall))
  }

  private fun saveHttpCallWithError(
    url: String,
    method: String,
    error: String,
    requestPayload: String
  ): Long {
    val requestHeaders = requestHeaders
    val httpCall = HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withError(error)
      .withPayload(requestPayload)
      .withRequestHeaders(requestHeaders)
      .build()
    httpCall.date = getDate(2017, 5, 2, 11, 22, 33)
    return snooperRepo!!.save(HttpCallRecord.from(httpCall))
  }

  private fun verifyClickActionOnShareMenu() {
    intended(
      allOf(
        hasExtras(
          EspressoIntentMatchers.forMailChooserIntent(
            ACTION_SEND,
            "*/*",
            "Log details",
            "2017_06_02_11_22_33.txt"
          )
        ),
        hasAction(ACTION_CHOOSER)
      )
    )
  }
}
