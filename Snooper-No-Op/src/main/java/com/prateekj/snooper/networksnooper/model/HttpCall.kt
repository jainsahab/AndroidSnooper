package com.prateekj.snooper.networksnooper.model

import java.util.Date

class HttpCall {
  val payload: String = ""
  val method: String = ""
  val url: String = ""
  val responseBody: String = ""
  val statusText: String = ""
  val statusCode: Int = -1
  var date: Date
    get() = Date()
    set(date) {}
  val requestHeaders = mapOf<String, List<String>>()
  val responseHeaders = mapOf<String, List<String>>()
  val error: String = ""

  class Builder {

    private val httpCall: HttpCall = HttpCall()

    fun withMethod(httpMethod: String) = this

    fun withUrl(url: String) = this

    fun build(): HttpCall {
      return httpCall
    }

    fun withPayload(payload: String) = this

    fun withResponseBody(responseBody: String) = this

    fun withStatusText(statusText: String) = this

    fun withStatusCode(rawStatusCode: Int) = this

    fun withRequestHeaders(headers: Map<String, List<String>>) = this

    fun withResponseHeaders(headers: Map<String, List<String>>) = this

    fun withError(error: String) = this
  }
}
