package com.prateekj.snooper.networksnooper.model

import java.util.Date

class HttpCall {

  var url: String? = null
  var payload: String? = null
  var method: String? = null
  var responseBody: String? = null
  var statusText: String? = null
  var statusCode: Int = 0
  var date: Date = Date()
  var requestHeaders = mapOf<String, List<String>>()
  var responseHeaders = mapOf<String, List<String>>()
  var error: String? = null

  class Builder {
    private val httpCall: HttpCall = HttpCall()

    fun withMethod(httpMethod: String) = apply {
      httpCall.method = httpMethod
    }

    fun withUrl(url: String) = apply {
      httpCall.url = url
    }

    fun build(): HttpCall {
      return httpCall
    }

    fun withPayload(payload: String) = apply {
      httpCall.payload = payload
    }

    fun withResponseBody(responseBody: String) = apply {
      httpCall.responseBody = responseBody
    }

    fun withStatusText(statusText: String) = apply {
      httpCall.statusText = statusText
    }

    fun withStatusCode(rawStatusCode: Int) = apply {
      httpCall.statusCode = rawStatusCode
    }

    fun withRequestHeaders(headers: Map<String, List<String>>) = apply {
      httpCall.requestHeaders = headers
    }

    fun withResponseHeaders(headers: Map<String, List<String>>) = apply {
      httpCall.responseHeaders = headers
    }

    fun withError(error: String) = apply {
      httpCall.error = error
    }
  }
}
