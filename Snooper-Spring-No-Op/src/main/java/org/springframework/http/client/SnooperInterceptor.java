package org.springframework.http.client;

import org.springframework.http.HttpRequest;

import java.io.IOException;

public class SnooperInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
    return execution.execute(httpRequest, bytes);
  }
}
