package com.prateekj.snooper.networksnooper.helper

import android.content.res.Resources
import com.prateekj.snooper.R
import com.prateekj.snooper.formatter.ResponseFormatter
import com.prateekj.snooper.formatter.ResponseFormatterFactory
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue
import com.prateekj.snooper.utils.TestUtils.getDate
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.ArrayList

class DataCopyHelperTest {

  private lateinit var formatterFactory: ResponseFormatterFactory
  private lateinit var httpCall: HttpCallRecord
  private lateinit var responseFormatter: ResponseFormatter
  private lateinit var dataCopyHelper: DataCopyHelper
  private lateinit var resources: Resources

  private val jsonContentTypeHeader: HttpHeader
    get() {
      val headerValue = HttpHeaderValue("application/json")
      val httpHeader = HttpHeader("Content-Type")
      httpHeader.values = listOf(headerValue)
      return httpHeader
    }

  private val header: HttpHeader
    get() {
      val headerValue = HttpHeaderValue("headerValue")
      val httpHeader = HttpHeader("Header")
      httpHeader.values = listOf(headerValue)
      return httpHeader
    }

  private val acceptLanguageHttpHeader: HttpHeader
    get() {
      val httpHeader = HttpHeader("accept-language")
      val value1 = HttpHeaderValue("en-US,en")
      val value2 = HttpHeaderValue("q=0.8,hi")
      val value3 = HttpHeaderValue("q=0.6")
      httpHeader.values = listOf(value1, value2, value3)
      return httpHeader
    }

  @Before
  @Throws(Exception::class)
  fun setUp() {
    formatterFactory = mockk(relaxed = true)
    httpCall = mockk(relaxed = true)
    responseFormatter = mockk(relaxed = true)
    resources = mockk(relaxed = true)
    dataCopyHelper = DataCopyHelper(httpCall, formatterFactory, resources)
    mockStringResources()
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyResponseWithoutFormattingIfContentHeadersNullInResponseData() {
    val responseBody = "response body"
    every { httpCall.getResponseHeader("Content-Type") } returns null
    every { httpCall.responseBody } returns responseBody
    every { formatterFactory.getFor("application/json") } returns responseFormatter

    val responseDataForCopy = dataCopyHelper.getResponseDataForCopy()

    assertThat(responseDataForCopy, `is`(responseBody))
    verify(exactly = 0) { responseFormatter.format(responseBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyResponseWithoutFormattingIfContentHeaderValuesIsMissingInResponseData() {
    val responseBody = "response body"
    val httpHeader = HttpHeader()
    httpHeader.values = ArrayList()
    every { httpCall.getResponseHeader("Content-Type") } returns httpHeader
    every { httpCall.responseBody } returns responseBody
    every { formatterFactory.getFor("application/json") } returns responseFormatter

    val responseDataForCopy = dataCopyHelper.getResponseDataForCopy()

    assertThat(responseDataForCopy, `is`(responseBody))
    verify(exactly = 0) { responseFormatter.format(responseBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyResponseWithFormattingIfContentHeadersPresentInResponseData() {
    val responseBody = "response body"
    val formattedResponseBody = "formatted response body"
    every { httpCall.getResponseHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.responseBody } returns responseBody
    every { formatterFactory.getFor("application/json") } returns responseFormatter
    every { responseFormatter.format(responseBody) } returns formattedResponseBody

    val responseDataForCopy = dataCopyHelper.getResponseDataForCopy()

    assertThat(responseDataForCopy, `is`(formattedResponseBody))
    verify { responseFormatter.format(responseBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyEmptyStringWhenResponseIsNotPresent() {
    every { httpCall.responseBody } returns null

    val responseDataForCopy = dataCopyHelper.getResponseDataForCopy()

    assertThat(responseDataForCopy, `is`(""))
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyRequestWithoutFormattingIfContentHeaderIsNullInRequestData() {
    val requestBody = "response body"
    every { httpCall.getRequestHeader("Content-Type") } returns null
    every { httpCall.payload } returns requestBody
    every { formatterFactory.getFor("application/json") } returns responseFormatter

    val requestDataForCopy = dataCopyHelper.getRequestDataForCopy()

    assertThat(requestDataForCopy, `is`(requestBody))
    verify(exactly = 0) { responseFormatter.format(requestBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyRequestWithoutFormattingIfContentHeaderValuesMissingInRequestData() {
    val requestBody = "response body"
    val httpHeader = HttpHeader()
    httpHeader.values = ArrayList()
    every { httpCall.getRequestHeader("Content-Type") } returns httpHeader
    every { httpCall.payload } returns requestBody
    every { formatterFactory.getFor("application/json") } returns responseFormatter

    val requestDataForCopy = dataCopyHelper.getRequestDataForCopy()

    assertThat(requestDataForCopy, `is`(requestBody))
    verify(exactly = 0) { responseFormatter.format(requestBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyRequestResponseHeadersPresent() {
    val httpHeader = acceptLanguageHttpHeader

    every { httpCall.requestHeaders } returns listOf(httpHeader, jsonContentTypeHeader)
    every { httpCall.getRequestHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.responseHeaders } returns listOf(httpHeader, header)
    every { httpCall.getResponseHeader("Content-Type") } returns jsonContentTypeHeader

    val copiedHeaders = dataCopyHelper.getHeadersForCopy()

    assertThat(
      copiedHeaders, `is`(
        "\nRequest Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
          "Content-Type: application/json\n\nResponse Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
          "Header: headerValue\n"
      )
    )
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyOnlyRequestHeadersPresentIfResponseHeadersMissing() {
    val httpHeader = acceptLanguageHttpHeader

    every { httpCall.requestHeaders } returns listOf(httpHeader, jsonContentTypeHeader)
    every { httpCall.responseHeaders } returns listOf()
    every { httpCall.getRequestHeader("Content-Type") } returns jsonContentTypeHeader

    val copiedHeaders = dataCopyHelper.getHeadersForCopy()

    assertThat(
      copiedHeaders,
      `is`("\nRequest Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" + "Content-Type: application/json\n")
    )
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyOnlyResponseHeadersPresentIfRequestHeadersMissing() {
    val httpHeader = acceptLanguageHttpHeader

    every { httpCall.requestHeaders } returns listOf()
    every { httpCall.responseHeaders } returns listOf(httpHeader, header)
    every { httpCall.getResponseHeader("Content-Type") } returns jsonContentTypeHeader

    val copiedHeaders = dataCopyHelper.getHeadersForCopy()

    assertThat(
      copiedHeaders,
      `is`("\nResponse Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" + "Header: headerValue\n")
    )
  }

  @Test
  @Throws(Exception::class)
  fun shouldCopyEmptyStringWhenRequestIsNotPresent() {
    every { httpCall.responseBody } returns null

    val requestDataForCopy = dataCopyHelper.getRequestDataForCopy()

    assertThat(requestDataForCopy, `is`(""))
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheError() {
    val error = "error"
    every { httpCall.error } returns error

    val errorsForCopy = dataCopyHelper.getErrorsForCopy()

    assertThat(errorsForCopy, `is`(error))
  }

  @Test
  @Throws(Exception::class)
  fun shouldShareRequestResponseData() {
    val requestBody = "request body"
    val formatRequestBody = "format Request body"
    val responseBody = "response body"
    val formatResponseBody = "format Response body"

    val httpHeader = acceptLanguageHttpHeader

    every { httpCall.requestHeaders } returns listOf(httpHeader, jsonContentTypeHeader)
    every { httpCall.getRequestHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.payload } returns requestBody
    every { httpCall.responseHeaders } returns listOf(httpHeader, header)
    every { httpCall.getResponseHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.responseBody } returns responseBody
    every { httpCall.date } returns getDate(2017, 4, 12, 1, 2, 3)
    every { formatterFactory.getFor("application/json") } returns responseFormatter
    every { responseFormatter.format(requestBody) } returns formatRequestBody
    every { responseFormatter.format(responseBody) } returns formatResponseBody

    val httpCallData = dataCopyHelper.getHttpCallData()

    assertThat(
      httpCallData.toString(), `is`(
        "Request Body\nformat Request body\nRequest Headers\naccept-language: en-US," +
          "en;q=0.8,hi;q=0.6\nContent-Type: application/json\nResponse Body\nformat Response body\nResponse Headers\n" +
          "accept-language: en-US,en;q=0.8,hi;q=0.6\nHeader: headerValue\n"
      )
    )
    verify(exactly = 1) { responseFormatter.format(requestBody)}
    verify(exactly = 1) { responseFormatter.format(responseBody)}
  }

  @Test
  @Throws(Exception::class)
  fun shouldShareResponseDataOnlyIfRequestDataEmpty() {
    val responseBody = "response body"
    val formatResponseBody = "format Response body"

    every { httpCall.getResponseHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.responseBody } returns responseBody
    every { httpCall.requestHeaders } returns null
    every { httpCall.responseHeaders } returns null
    every { httpCall.date } returns getDate(2017, 4, 12, 1, 2, 3)
    every { formatterFactory.getFor("application/json") } returns responseFormatter
    every { responseFormatter.format(responseBody) } returns formatResponseBody

    val httpCallData = dataCopyHelper.getHttpCallData()

    assertThat(httpCallData.toString(), `is`("Response Body\nformat Response body"))
    verify(exactly = 1) { responseFormatter.format(responseBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShareRequestDataOnlyIfResponseDataEmpty() {
    val requestBody = "request body"
    val formatRequestBody = "format Request body"

    every { httpCall.getRequestHeader("Content-Type") } returns jsonContentTypeHeader
    every { httpCall.payload } returns requestBody
    every { httpCall.responseBody } returns null
    every { httpCall.requestHeaders } returns null
    every { httpCall.responseHeaders } returns null
    every { httpCall.date } returns getDate(2017, 4, 12, 1, 2, 3)
    every { formatterFactory.getFor("application/json") } returns responseFormatter
    every { responseFormatter.format(requestBody) } returns formatRequestBody

    val httpCallData = dataCopyHelper.getHttpCallData()

    assertThat(httpCallData.toString(), `is`("Request Body\nformat Request body"))
    verify(exactly = 1) { responseFormatter.format(requestBody) }
  }

  private fun mockStringResources() {
    every { resources.getString(R.string.request_body_heading) } returns "Request Body"
    every { resources.getString(R.string.request_headers) } returns "Request Headers"
    every { resources.getString(R.string.response_body_heading) } returns "Response Body"
    every { resources.getString(R.string.response_headers) } returns "Response Headers"
  }
}