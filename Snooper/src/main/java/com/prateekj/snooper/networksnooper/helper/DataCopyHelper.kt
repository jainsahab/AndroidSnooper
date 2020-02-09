package com.prateekj.snooper.networksnooper.helper

import android.content.res.Resources
import com.prateekj.snooper.R
import com.prateekj.snooper.formatter.ResponseFormatterFactory
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.model.HttpHeader.Companion.CONTENT_TYPE

class DataCopyHelper(
  private val httpCallRecord: HttpCallRecord,
  private val responseFormatterFactory: ResponseFormatterFactory,
  private val resources: Resources
) {

  fun getResponseDataForCopy(): String {
    return getFormattedData(
      httpCallRecord.getResponseHeader(CONTENT_TYPE),
      httpCallRecord.responseBody
    )
  }

  fun getRequestDataForCopy(): String {
    return getFormattedData(
      httpCallRecord.getRequestHeader(CONTENT_TYPE),
      httpCallRecord.payload
    )

  }

  fun getErrorsForCopy(): String? = httpCallRecord.error

  fun getHeadersForCopy(): String {
    val dataToCopy = StringBuilder()
    appendHeaders(
      httpCallRecord.requestHeaders,
      dataToCopy,
      resources.getString(R.string.request_headers)
    )
    appendHeaders(
      httpCallRecord.responseHeaders,
      dataToCopy,
      resources.getString(R.string.response_headers)
    )
    return dataToCopy.toString()
  }

  fun getHttpCallData(): StringBuilder {
    val dataToCopy = StringBuilder()
    appendRequestData(dataToCopy)
    appendResponseData(dataToCopy)
    return dataToCopy
  }

  private fun appendRequestData(dataToCopy: StringBuilder) {
    val formattedRequestData = getRequestDataForCopy()
    if (formattedRequestData.isNotEmpty()) {
      val heading = resources.getString(R.string.request_body_heading)
      dataToCopy.append("$heading\n$formattedRequestData")
    }
    appendHeaders(
      httpCallRecord.requestHeaders,
      dataToCopy,
      resources.getString(R.string.request_headers)
    )
  }

  private fun appendHeaders(
    headers: List<HttpHeader>?,
    dataToCopy: StringBuilder,
    heading: String
  ) {
    if (headers != null && headers.isNotEmpty()) {
      dataToCopy.append("\n$heading\n")
      headers.forEach {
        dataToCopy.append("${it.name}: ${toHeaderValues(it)}\n")
      }
    }
  }

  private fun toHeaderValues(httpHeader: HttpHeader): String {
    return httpHeader.values.joinToString(separator = ";") { it.value }
  }

  private fun appendResponseData(dataToCopy: StringBuilder) {
    val formattedResponseData = getResponseDataForCopy()

    if (formattedResponseData.isNotEmpty()) {
      val heading = resources.getString(R.string.response_body_heading)
      dataToCopy.append("$heading\n$formattedResponseData")
    }
    appendHeaders(
      httpCallRecord.responseHeaders,
      dataToCopy,
      resources.getString(R.string.response_headers)
    )
  }


  private fun getFormattedData(contentTypeHeader: HttpHeader?, dataToCopy: String?): String {
    if (dataToCopy == null) {
      return ""
    }
    if (contentHeadersPresent(contentTypeHeader)) {
      val formatter = this.responseFormatterFactory.getFor(contentTypeHeader!!.values[0].value)
      return formatter.format(dataToCopy)
    }
    return dataToCopy
  }

  private fun contentHeadersPresent(contentTypeHeader: HttpHeader?): Boolean {
    return contentTypeHeader != null && contentTypeHeader.values.size > 0
  }
}
