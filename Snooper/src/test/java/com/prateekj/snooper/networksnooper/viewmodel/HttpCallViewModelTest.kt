package com.prateekj.snooper.networksnooper.viewmodel

import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCall.Builder
import com.prateekj.snooper.utils.TestUtils

import org.junit.Before
import org.junit.Test

import java.util.Arrays
import java.util.HashMap

import android.view.View.GONE
import android.view.View.VISIBLE
import com.prateekj.snooper.networksnooper.model.HttpCallRecord.Companion
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue

class HttpCallViewModelTest {
  private lateinit var httpCall: HttpCall
  private lateinit var httpCallViewModel: HttpCallViewModel

  @Before
  @Throws(Exception::class)
  fun setUp() {
    val url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0"

    httpCall = Builder()
      .withUrl(url)
      .withMethod("POST")
      .withStatusCode(200)
      .withStatusText("OK")
      .withResponseHeaders(singleHeader())
      .withRequestHeaders(singleHeader())
      .build()

    val currentDate = TestUtils.getDate(2017, 5, 2, 11, 22, 33)
    httpCall.date = currentDate

    httpCallViewModel = HttpCallViewModel(Companion.from(httpCall))
  }

  @Test
  @Throws(Exception::class)
  fun getUrl() {
    assertTrue(httpCallViewModel.url == httpCall.url)
  }

  @Test
  @Throws(Exception::class)
  fun getMethod() {
    assertTrue(httpCallViewModel.method == httpCall.method)
  }

  @Test
  @Throws(Exception::class)
  fun getStatusCode() {
    assertThat(httpCallViewModel.statusCode, `is`("200"))
  }

  @Test
  @Throws(Exception::class)
  fun getStatusText() {
    assertTrue(httpCallViewModel.statusText == httpCall.statusText)
  }

  @Test
  @Throws(Exception::class)
  fun getTimeStamp() {
    assertTrue(httpCallViewModel.timeStamp == "06/02/2017 11:22:33")
  }

  @Test
  @Throws(Exception::class)
  fun getRequestHeaders() {
    assertThat<String>(httpCallViewModel.requestHeaders[0].name, `is`("header1"))
  }

  @Test
  @Throws(Exception::class)
  fun getResponseHeaders() {
    assertThat<String>(httpCallViewModel.responseHeaders[0].name, `is`("header1"))
  }

  @Test
  fun shouldGetColorGreenWhenStatusCode2xx() {
    assertEquals(httpCallViewModel.getStatusColor().toLong(), R.color.snooper_green.toLong())
  }

  @Test
  fun shouldGetResponseHeaderVisibilityAsGone() {
    val httpCallViewModel = HttpCallViewModel(Companion.from(Builder().build()))
    assertThat(httpCallViewModel.responseHeaderVisibility, `is`(GONE))
  }

  @Test
  fun shouldGetRequestHeaderVisibilityAsGone() {
    val httpCallViewModel = HttpCallViewModel(Companion.from(Builder().build()))
    assertThat(httpCallViewModel.requestHeaderVisibility, `is`(GONE))
  }

  @Test
  fun shouldGetColorYellowWhenStatusCode3xx() {
    val httpCall = Builder()
      .withUrl(" url 1")
      .withMethod("POST")
      .withStatusCode(302)
      .withStatusText("FAIL")
      .build()
    val viewModel = HttpCallViewModel(Companion.from(httpCall))
    assertEquals(viewModel.getStatusColor().toLong(), R.color.snooper_yellow.toLong())
  }

  @Test
  fun shouldGetColorRedWhenStatusCode4xx() {
    val httpCall = Builder()
      .withUrl(" url 1")
      .withMethod("POST")
      .withStatusCode(400)
      .withStatusText("FAIL")
      .build()
    val viewModel = HttpCallViewModel(Companion.from(httpCall))
    assertEquals(viewModel.getStatusColor().toLong(), R.color.snooper_red.toLong())
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnResponseInfoContainerVisibilityAsVisible() {
    assertThat(httpCallViewModel.responseInfoVisibility, `is`(VISIBLE))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnResponseInfoContainerVisibilityAsGone() {
    val httpCall = Builder().withError("error").build()
    val httpCallViewModel = HttpCallViewModel(Companion.from(httpCall))
    assertThat(httpCallViewModel.responseInfoVisibility, `is`(GONE))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnFailedTextVisibilityAsGone() {
    assertThat(httpCallViewModel.failedTextVisibility, `is`(GONE))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnFailedTextVisibilityAsVisible() {
    val httpCall = Builder().withError("error").build()
    val httpCallViewModel = HttpCallViewModel(Companion.from(httpCall))
    assertThat(httpCallViewModel.failedTextVisibility, `is`(VISIBLE))
  }

  @Test
  fun shouldGetResponseHeaderVisibilityAsVisible() {
    assertThat(httpCallViewModel.responseHeaderVisibility, `is`(VISIBLE))
  }

  @Test
  fun shouldGetRequestHeaderVisibilityAsVisible() {
    assertThat(httpCallViewModel.requestHeaderVisibility, `is`(VISIBLE))
  }

  private fun singleHeader(): HashMap<String, List<String>> {
    return object : HashMap<String, List<String>>() {
      init {
        put("header1", listOf("headerValue"))
      }
    }
  }
}