package org.springframework.http.client

import com.prateekj.snooper.networksnooper.model.HttpHeader.Companion.CONTENT_TYPE
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus.OK
import java.io.ByteArrayInputStream
import java.io.UnsupportedEncodingException
import java.net.URI.create
import java.net.UnknownHostException
import java.util.Arrays

class SpringHttpRequestTransformerTest {

  @Test
  @Throws(Exception::class)
  fun shouldTransformHttpCallFromSpringHttpRequest() {
    val url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0"
    val uri = create(url)
    val responseBody = "responseBody"
    val requestBody = "requestBody"
    val httpHeaders = HttpHeaders()
    httpHeaders["Content-Type"] = listOf("application/json")

    val httpResponse = mockk<ClientHttpResponse>(relaxed = true)
    every { httpResponse.body } returns ByteArrayInputStream(toBytes(responseBody))
    every { httpResponse.statusCode } returns OK
    every { httpResponse.rawStatusCode } returns 200
    every { httpResponse.headers } returns httpHeaders
    val httpRequest = mockk<HttpRequest>()
    every { httpRequest.method } returns POST
    every { httpRequest.uri } returns uri
    every { httpRequest.headers } returns httpHeaders

    val transformer = SpringHttpRequestTransformer()

    val httpCall = transformer.transform(httpRequest, toBytes(requestBody), httpResponse)

    assertThat<String>(httpCall.method, `is`("POST"))
    assertThat<String>(httpCall.payload, `is`(requestBody))
    assertThat<String>(httpCall.url, `is`(url))
    assertThat<String>(httpCall.responseBody, `is`(responseBody))
    assertThat<String>(httpCall.statusText, `is`("OK"))
    assertThat(httpCall.statusCode, `is`(200))
    assertThat(httpCall.requestHeaders.size, `is`(1))
    assertThat(httpCall.responseHeaders.size, `is`(1))
    assertNotNull(httpCall.responseHeaders[CONTENT_TYPE])
    assertNotNull(httpCall.requestHeaders[CONTENT_TYPE])
  }

  @Test
  @Throws(Exception::class)
  fun shouldTransformHttpCallFromClientSideError() {
    val url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0"
    val uri = create(url)
    val requestBody = "requestBody"
    val httpHeaders = HttpHeaders()
    httpHeaders["Content-Type"] = listOf("application/json")

    val httpRequest = mockk<HttpRequest>()
    every { httpRequest.method } returns POST
    every { httpRequest.uri } returns uri
    every { httpRequest.headers } returns httpHeaders

    val ioException = UnknownHostException("Unable to connect")

    val transformer = SpringHttpRequestTransformer()

    val httpCall = transformer.transform(httpRequest, toBytes(requestBody), ioException)

    assertThat<String>(httpCall.method, `is`("POST"))
    assertThat<String>(httpCall.payload, `is`(requestBody))
    assertThat<String>(httpCall.url, `is`(url))
    assertThat(httpCall.requestHeaders.size, `is`(1))
    assertNotNull(httpCall.requestHeaders[CONTENT_TYPE])
    assertThat<String>(httpCall.error, `is`("java.net.UnknownHostException: Unable to connect"))
  }

  fun toBytes(string: String): ByteArray {
    try {
      return string.toByteArray(charset("UTF-8"))
    } catch (e: UnsupportedEncodingException) {
      e.printStackTrace()
    }

    return ByteArray(0)
  }
}