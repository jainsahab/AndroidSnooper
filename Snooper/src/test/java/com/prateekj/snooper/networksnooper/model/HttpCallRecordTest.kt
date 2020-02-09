package com.prateekj.snooper.networksnooper.model

import org.junit.Before
import org.junit.Test

import java.util.Arrays
import java.util.HashMap

import java.util.Collections.singletonList
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class HttpCallRecordTest {
  private var httpCallRecord: HttpCallRecord? = null

  @Before
  @Throws(Exception::class)
  fun setUp() {
    val url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0"
    val responseBody = "responseBody"
    val requestBody = "requestBody"
    httpCallRecord = HttpCallRecord.from(
      HttpCall.Builder()
        .withUrl(url)
        .withMethod("POST")
        .withPayload(requestBody)
        .withResponseBody(responseBody)
        .withStatusCode(200)
        .withStatusText("OK")
        .withRequestHeaders(getRequestHeaders())
        .withResponseHeaders(getResponseHeaders())
        .build()
    )
    assertNotNull(httpCallRecord!!.date)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnRequestHeaderByGivenName() {
    val requestHeader = httpCallRecord!!.getRequestHeader("User-Agent")
    assertThat(requestHeader!!.values[0].value, `is`("Android Browser"))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnRequestHeaderByGivenNameByIgnoringCase() {
    val requestHeader = httpCallRecord!!.getRequestHeader("USER-AGENT")
    assertThat(requestHeader!!.values[0].value, `is`("Android Browser"))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnNullWhenHeaderByGivenNameNotFound() {
    val requestHeader = httpCallRecord!!.getRequestHeader("Invalid Name")
    assertNull(requestHeader)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnResponseHeaderByGivenName() {
    val responseHeader = httpCallRecord!!.getResponseHeader("date")
    assertThat(responseHeader!!.values[0].value, `is`("Thu, 02 Mar 2017 13:03:11 GMT"))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnResponseHeaderByGivenNameByIgnoringCase() {
    val responseHeader = httpCallRecord!!.getResponseHeader("DATE")
    assertThat(responseHeader!!.values[0].value, `is`("Thu, 02 Mar 2017 13:03:11 GMT"))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnNullWhenResponseHeaderByGivenNameNotFound() {
    val responseHeader = httpCallRecord!!.getResponseHeader("Invalid Name")
    assertNull(responseHeader)
  }

  private fun getResponseHeaders(): Map<String, List<String>> {
    val xssProtectionHeader = listOf("1", "mode=block")
    val dateHeader = listOf("Thu, 02 Mar 2017 13:03:11 GMT")
    return mapOf(
      "x-xss-protection" to xssProtectionHeader,
      "date" to dateHeader
    )
  }

  private fun getRequestHeaders(): Map<String, List<String>> {
    val cacheControlHeader = Arrays.asList("public", "max-age=86400", "no-transform")
    val userAgentHeader = listOf("Android Browser")
    return mapOf(
      "User-Agent" to userAgentHeader,
      "cache-control" to cacheControlHeader
    )
  }
}