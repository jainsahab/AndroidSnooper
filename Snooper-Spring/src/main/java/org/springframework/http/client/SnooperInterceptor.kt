package org.springframework.http.client

import com.prateekj.snooper.AndroidSnooper
import org.springframework.http.HttpRequest
import java.io.IOException

class SnooperInterceptor : ClientHttpRequestInterceptor {

  @Throws(IOException::class)
  override fun intercept(
    request: HttpRequest, byteArray: ByteArray,
    execution: ClientHttpRequestExecution
  ): ClientHttpResponse {

    val transformer = SpringHttpRequestTransformer()
    val snooper = AndroidSnooper.instance
    val streamResponse: ClientHttpResponse
    try {
      streamResponse = execution.execute(request, byteArray)
    } catch (e: Exception) {
      val call = transformer.transform(request, byteArray, e)
      snooper.record(call)
      throw e
    }

    val httpResponse = BufferingClientHttpResponseWrapper(streamResponse)
    val call = transformer.transform(request, byteArray, httpResponse)
    snooper.record(call)
    return httpResponse
  }
}