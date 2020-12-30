package com.prateekj.snooper.networksnooper.database

import androidx.test.InstrumentationRegistry.getTargetContext
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue
import com.prateekj.snooper.rules.DataResetRule
import com.prateekj.snooper.utils.TestUtilities.getCalendar
import com.prateekj.snooper.utils.TestUtilities.getDate
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Arrays
import java.util.Calendar
import java.util.Calendar.DATE
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.YEAR
import java.util.Date

class SnooperRepoTest {

  @get:Rule
  var rule = DataResetRule()
  private var repo: SnooperRepo? = null

  private fun getResponseHeaders(): Map<String, List<String>> {
    val xssProtectionHeader = listOf("1", "mode=block")
    val dateHeader = listOf("Thu, 02 Mar 2017 13:03:11 GMT")
    return mapOf(
      "x-xss-protection" to xssProtectionHeader,
      "date" to dateHeader
    )
  }

  private fun getRequestHeaders(): Map<String, List<String>> {
    val cacheControlHeader = listOf("public", "max-age=86400", "no-transform")
    val userAgentHeader = listOf("Android Browser")
    return mapOf(
      "User-Agent" to userAgentHeader,
      "cache-control" to cacheControlHeader
    )
  }

  @Before
  @Throws(Exception::class)
  fun setUp() {
    repo = SnooperRepo(getTargetContext())
  }

  @Test
  @Throws(Exception::class)
  fun shouldSaveAndRetrieveHttpCallRecord() {
    val httpCall =
      HttpCall.Builder().withUrl("http://google.com").withMethod("POST").withStatusCode(200)
        .withStatusText("OK").withPayload("payload").withResponseBody("responseBody")
        .withRequestHeaders(getRequestHeaders()).withResponseHeaders(getResponseHeaders())
        .withError("error").build()

    val id = repo!!.save(HttpCallRecord.from(httpCall))
    val httpCallRecord = repo!!.findById(id)

    assertThat<String>(httpCallRecord.url, `is`("http://google.com"))
    assertThat<String>(httpCallRecord.method, `is`("POST"))
    assertThat(httpCallRecord.statusCode, `is`(200))
    assertThat<String>(httpCallRecord.statusText, `is`("OK"))
    assertThat<String>(httpCallRecord.payload, `is`("payload"))
    assertThat<String>(httpCallRecord.responseBody, `is`("responseBody"))
    assertThat<String>(httpCallRecord.error, `is`("error"))

    val userAgentHeader = httpCallRecord.getRequestHeader("User-Agent")
    assertThat(userAgentHeader!!.values, containsWithValue("Android Browser"))
    val cacheControlHeader = httpCallRecord.getRequestHeader("cache-control")
    assertThat(cacheControlHeader!!.values, containsWithValue("public"))
    assertThat(cacheControlHeader.values, containsWithValue("max-age=86400"))
    assertThat(cacheControlHeader.values, containsWithValue("no-transform"))

    val dateHeader = httpCallRecord.getResponseHeader("date")
    assertThat(dateHeader!!.values, containsWithValue("Thu, 02 Mar 2017 13:03:11 GMT"))
    val xssProtectionHeader = httpCallRecord.getResponseHeader("x-xss-protection")
    assertThat(xssProtectionHeader!!.values, containsWithValue("1"))
    assertThat(xssProtectionHeader.values, containsWithValue("mode=block"))

  }

  @Test
  @Throws(Exception::class)
  fun shouldGetAllHttpCallsInTheDateDescendingOrder() {
    val beforeDate = getDate(2016, 5, 23)
    val afterDate = getDate(2016, 5, 24)
    val beforeHttpCall = HttpCall.Builder().withUrl("url1").build()
    val afterHttpCall = HttpCall.Builder().withUrl("url2").build()
    beforeHttpCall.date = beforeDate
    afterHttpCall.date = afterDate
    repo!!.save(HttpCallRecord.from(beforeHttpCall))
    repo!!.save(HttpCallRecord.from(afterHttpCall))

    val httpCalls = repo!!.findAllSortByDate()

    assertThat(httpCalls, hasCallWithUrl("url1"))
    assertThat(httpCalls, hasCallWithUrl("url2"))
    assertThat(httpCalls[0], hasDate(getCalendar(afterDate)))
    assertThat(httpCalls[1], hasDate(getCalendar(beforeDate)))
  }

  @Test
  @Throws(Exception::class)
  fun shouldSearchHttpCallsByUrlInTheDateDescendingOrder() {
    val beforeDate = getDate(2016, 5, 23)
    val afterDate = getDate(2016, 5, 24)
    val beforeHttpCall = HttpCall.Builder().withUrl("url1").build()
    val afterHttpCall = HttpCall.Builder().withUrl("url2").build()
    beforeHttpCall.date = beforeDate
    afterHttpCall.date = afterDate
    repo!!.save(HttpCallRecord.from(beforeHttpCall))
    repo!!.save(HttpCallRecord.from(afterHttpCall))

    val httpCalls = repo!!.searchHttpRecord("url")

    assertThat(httpCalls, hasCallWithUrl("url1"))
    assertThat(httpCalls, hasCallWithUrl("url2"))
    assertThat(httpCalls[0], hasDate(getCalendar(afterDate)))
    assertThat(httpCalls[1], hasDate(getCalendar(beforeDate)))
  }

  @Test
  @Throws(Exception::class)
  fun shouldSearchHttpCallsByRequestBodyInTheDateDescendingOrder() {
    val beforeDate = getDate(2016, 5, 23)
    val afterDate = getDate(2016, 5, 24)
    val beforeHttpCall = HttpCall.Builder().withPayload("requestBody1").build()
    val afterHttpCall = HttpCall.Builder().withPayload("requestBody2").build()
    beforeHttpCall.date = beforeDate
    afterHttpCall.date = afterDate
    repo!!.save(HttpCallRecord.from(beforeHttpCall))
    repo!!.save(HttpCallRecord.from(afterHttpCall))

    val httpCalls = repo!!.searchHttpRecord("request")

    assertThat<String>(httpCalls[0].payload, `is`("requestBody2"))
    assertThat<String>(httpCalls[1].payload, `is`("requestBody1"))
    assertThat(httpCalls[0], hasDate(getCalendar(afterDate)))
    assertThat(httpCalls[1], hasDate(getCalendar(beforeDate)))
  }

  @Test
  @Throws(Exception::class)
  fun shouldSearchHttpCallsByResponseBodyInTheDateDescendingOrder() {
    val beforeDate = getDate(2016, 5, 23)
    val afterDate = getDate(2016, 5, 24)
    val beforeHttpCall = HttpCall.Builder().withResponseBody("responseBody1").build()
    val afterHttpCall = HttpCall.Builder().withResponseBody("responseBody2").build()
    beforeHttpCall.date = beforeDate
    afterHttpCall.date = afterDate
    repo!!.save(HttpCallRecord.from(beforeHttpCall))
    repo!!.save(HttpCallRecord.from(afterHttpCall))

    val httpCalls = repo!!.searchHttpRecord("response")

    assertThat<String>(httpCalls[0].responseBody, `is`("responseBody2"))
    assertThat<String>(httpCalls[1].responseBody, `is`("responseBody1"))
    assertThat(httpCalls[0], hasDate(getCalendar(afterDate)))
    assertThat(httpCalls[1], hasDate(getCalendar(beforeDate)))
  }

  @Test
  @Throws(Exception::class)
  fun shouldSearchHttpCallsByErrorInTheDateDescendingOrder() {
    val beforeDate = getDate(2016, 5, 23)
    val afterDate = getDate(2016, 5, 24)
    val beforeHttpCall = HttpCall.Builder().withError("error1").build()
    val afterHttpCall = HttpCall.Builder().withError("error2").build()
    beforeHttpCall.date = beforeDate
    afterHttpCall.date = afterDate
    repo!!.save(HttpCallRecord.from(beforeHttpCall))
    repo!!.save(HttpCallRecord.from(afterHttpCall))

    val httpCalls = repo!!.searchHttpRecord("error")

    assertThat<String>(httpCalls[0].error, `is`("error2"))
    assertThat<String>(httpCalls[1].error, `is`("error1"))
    assertThat(httpCalls[0], hasDate(getCalendar(afterDate)))
    assertThat(httpCalls[1], hasDate(getCalendar(beforeDate)))
  }

  @Test
  @Throws(Exception::class)
  fun shouldGetNextSetOfHttpCallsAfterTheGivenId() {
    saveCalls(50)

    var httpCalls = repo!!.findAllSortByDateAfter(-1, 20)
    assertThat(httpCalls.size, `is`(20))
    assertThat<List<HttpCallRecord>>(httpCalls, areSortedAccordingToDate())

    httpCalls = repo!!.findAllSortByDateAfter(httpCalls.last().id, 20)
    assertThat(httpCalls.size, `is`(20))
    assertThat<List<HttpCallRecord>>(httpCalls, areSortedAccordingToDate())

    httpCalls = repo!!.findAllSortByDateAfter(httpCalls.last().id, 20)
    assertThat(httpCalls.size, `is`(10))
    assertThat<List<HttpCallRecord>>(httpCalls, areSortedAccordingToDate())
  }

  private fun areSortedAccordingToDate(): CustomTypeSafeMatcher<List<HttpCallRecord>> {
    return object : CustomTypeSafeMatcher<List<HttpCallRecord>>("are sorted") {
      override fun matchesSafely(list: List<HttpCallRecord>): Boolean {
        for (index in 0 until list.size - 1) {
          val firstRecordTime = list[index].date!!.time
          val secondRecordTime = list[index + 1].date!!.time
          if (firstRecordTime < secondRecordTime) {
            return false
          }
        }
        return true
      }
    }
  }

  @Test
  @Throws(Exception::class)
  fun shouldDeleteAllTheRecords() {
    val httpCall = HttpCall.Builder()
      .withUrl("url1")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build()
    val httpCall2 = HttpCall.Builder()
      .withUrl("url2")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build()
    repo!!.save(HttpCallRecord.from(httpCall))
    repo!!.save(HttpCallRecord.from(httpCall2))

    repo!!.deleteAll()

    val httpCalls = repo!!.findAllSortByDate()
    assertThat(httpCalls.size, `is`(0))
  }

  private fun saveCalls(number: Int) {
    for (i in 0 until number) {
      val httpCall = HttpCall.Builder().withUrl("url$i").build()
      val date = Date(getDate(2016, 5, 23).time + i * 1000)
      httpCall.date = date
      repo!!.save(HttpCallRecord.from(httpCall))
    }
  }

  private fun containsWithValue(value: String): Matcher<in List<HttpHeaderValue>> {
    return object : CustomTypeSafeMatcher<List<HttpHeaderValue>>("contains with value:$value") {
      override fun matchesSafely(list: List<HttpHeaderValue>): Boolean {
        return list.any { httpHeaderValue -> httpHeaderValue.value == value }
      }
    }
  }

  private fun hasCallWithUrl(url: String): CustomTypeSafeMatcher<List<HttpCallRecord>> {
    return object : CustomTypeSafeMatcher<List<HttpCallRecord>>("with url") {
      override fun matchesSafely(item: List<HttpCallRecord>): Boolean {
        for (httpCall in item) {
          if (httpCall.url == url) {
            return true
          }
        }
        return false
      }
    }
  }

  private fun hasDate(date: Calendar): Matcher<in HttpCallRecord> {
    return object : CustomTypeSafeMatcher<HttpCallRecord>("has date: $date") {
      override fun matchesSafely(item: HttpCallRecord): Boolean {
        val actualCalendar = Calendar.getInstance()
        actualCalendar.time = item.date
        assertThat(actualCalendar.get(DATE), `is`(date.get(DATE)))
        assertThat(actualCalendar.get(DAY_OF_MONTH), `is`(date.get(DAY_OF_MONTH)))
        assertThat(actualCalendar.get(YEAR), `is`(date.get(YEAR)))
        return true
      }
    }
  }

}