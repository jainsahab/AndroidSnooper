package com.prateekj.snooper.networksnooper.viewmodel

import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue

import org.junit.Before
import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class HttpHeaderViewModelTest {

  private lateinit var httpHeaderViewModel: HttpHeaderViewModel

  @Before
  @Throws(Exception::class)
  fun setUp() {
    val httpHeader = HttpHeader("accept-language")
    val value1 = HttpHeaderValue("en-US,en")
    val value2 = HttpHeaderValue("q=0.8,hi")
    val value3 = HttpHeaderValue("q=0.6")
    httpHeader.values = listOf(value1, value2, value3)
    httpHeaderViewModel = HttpHeaderViewModel(httpHeader)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnHeaderName() {
    assertThat(httpHeaderViewModel.headerName(), `is`("accept-language"))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnHeaderValues() {
    assertThat(httpHeaderViewModel.headerValues(), `is`("en-US,en;q=0.8,hi;q=0.6"))
  }
}