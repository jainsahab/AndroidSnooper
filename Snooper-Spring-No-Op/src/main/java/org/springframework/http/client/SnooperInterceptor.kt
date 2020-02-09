package org.springframework.http.client

import org.springframework.http.HttpRequest

import java.io.IOException

class SnooperInterceptor : ClientHttpRequestInterceptor {
  @Throws(IOException::class)
  override fun intercept(
    httpRequest: HttpRequest,
    bytes: ByteArray,
    execution: ClientHttpRequestExecution
  ): ClientHttpResponse {
    return execution.execute(httpRequest, bytes)
  }
}
