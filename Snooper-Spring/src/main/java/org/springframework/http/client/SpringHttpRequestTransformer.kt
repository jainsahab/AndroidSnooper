package org.springframework.http.client

import com.prateekj.snooper.networksnooper.model.HttpCall
import org.springframework.http.HttpRequest
import java.io.IOException

class SpringHttpRequestTransformer {

  @Throws(IOException::class)
  fun transform(
    httpRequest: HttpRequest,
    requestPayload: ByteArray,
    httpResponse: ClientHttpResponse
  ): HttpCall {
    return HttpCall.Builder()
      .withUrl(httpRequest.uri.toString())
      .withPayload(String(requestPayload))
      .withMethod(httpRequest.method.toString())
      .withResponseBody(httpResponse.body.bufferedReader().use { it.readText() })
      .withStatusCode(httpResponse.rawStatusCode)
      .withStatusText(httpResponse.statusCode.reasonPhrase)
      .withRequestHeaders(httpRequest.headers)
      .withResponseHeaders(httpResponse.headers)
      .build()
  }

  @Throws(IOException::class)
  fun transform(httpRequest: HttpRequest, requestPayload: ByteArray, e: Exception): HttpCall {
    return HttpCall.Builder()
      .withUrl(httpRequest.uri.toString())
      .withPayload(String(requestPayload))
      .withMethod(httpRequest.method.toString())
      .withRequestHeaders(httpRequest.headers)
      .withError(e.toString())
      .build()
  }
}
