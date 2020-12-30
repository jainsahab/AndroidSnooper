package com.prateekj.snooper.okhttp

import com.prateekj.snooper.AndroidSnooper
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.utils.Logger

import java.io.IOException
import java.util.HashMap

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

import okhttp3.ResponseBody.create

class SnooperInterceptor : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val response: Response
    val responseBody: String
    val request = chain.request()
    val builder = HttpCall.Builder()
      .withUrl(request.url().toString())
      .withPayload(getRequestBody(request))
      .withMethod(request.method())
      .withRequestHeaders(headers(request.headers()))
    try {
      response = chain.proceed(request)
      responseBody = response.body().string()
    } catch (e: Exception) {
      val httpCall = builder.withError(e.toString()).build()
      AndroidSnooper.instance.record(httpCall)
      throw e
    }

    val httpCall = builder.withResponseBody(responseBody)
      .withStatusCode(response.code())
      .withStatusText(response.message())
      .withResponseHeaders(headers(response.headers())).build()
    AndroidSnooper.instance.record(httpCall)
    return response.newBuilder().body(create(response.body().contentType(), responseBody))
      .build()
  }

  private fun headers(headers: Headers): Map<String, List<String>> {
    val extractedHeaders = HashMap<String, List<String>>()
    for (headerName in headers.names()) {
      extractedHeaders[headerName] = headers.values(headerName)
    }
    return extractedHeaders
  }

  private fun getRequestBody(request: Request): String {
    try {
      val copy = request.newBuilder().build()
      val buffer = Buffer()
      if (copy.body() == null) {
        return ""
      }
      copy.body().writeTo(buffer)
      return buffer.readUtf8()
    } catch (e: IOException) {
      Logger.e(TAG, "couldn't retrieve request body", e)
      return ""
    }

  }

  companion object {

    val TAG: String = SnooperInterceptor::class.java.simpleName
  }
}
