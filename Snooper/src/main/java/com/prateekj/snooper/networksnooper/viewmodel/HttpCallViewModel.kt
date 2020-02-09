package com.prateekj.snooper.networksnooper.viewmodel

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.ColorRes
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeader
import java.text.SimpleDateFormat
import java.util.Locale.US

class HttpCallViewModel(private val httpCall: HttpCallRecord) {

  val url: String? = httpCall.url

  val method: String? = httpCall.method

  val statusCode: String = httpCall.statusCode.toString()

  val statusText: String? = httpCall.statusText

  val requestHeaders: List<HttpHeader> = httpCall.requestHeaders ?: listOf()

  val responseHeaders: List<HttpHeader> = httpCall.responseHeaders ?: listOf()

  val timeStamp: String
    get() {
      val df = SimpleDateFormat(TIMESTAMP_FORMAT, US)
      return df.format(httpCall.date)
    }

  val responseInfoVisibility: Int = if (httpCall.hasError()) GONE else VISIBLE

  val failedTextVisibility: Int = if (httpCall.hasError()) VISIBLE else GONE

  val responseHeaderVisibility: Int = if (hasHeaders(httpCall.responseHeaders)) VISIBLE else GONE

  val requestHeaderVisibility: Int = if (hasHeaders(httpCall.requestHeaders)) VISIBLE else GONE

  @ColorRes
  fun getStatusColor(): Int {
    val statusCode = httpCall.statusCode
    return when {
      statusCode in RANGE_START_HTTP_OK..RANGE_END_HTTP_OK -> R.color.snooper_green
      statusCode <= RANGE_END_HTTP_REDIRECTION -> R.color.snooper_yellow
      else -> R.color.snooper_red
    }
  }

  private fun hasHeaders(headers: List<HttpHeader>?): Boolean {
    return headers != null && headers.isNotEmpty()
  }

  companion object {
    private const val TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss"
    private const val RANGE_START_HTTP_OK = 200
    private const val RANGE_END_HTTP_OK = 299
    private const val RANGE_END_HTTP_REDIRECTION = 399
  }
}
