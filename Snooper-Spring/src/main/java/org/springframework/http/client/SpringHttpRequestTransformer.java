package org.springframework.http.client;

import com.prateekj.snooper.model.HttpCall;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpRequest;

import java.io.IOException;
import java.nio.charset.Charset;

public class SpringHttpRequestTransformer {

  public HttpCall transform(HttpRequest httpRequest, byte[] requestPayload, ClientHttpResponse httpResponse) throws IOException {
    return new HttpCall.Builder()
      .withUrl(httpRequest.getURI().toString())
      .withPayload(new String(requestPayload))
      .withMethod(httpRequest.getMethod().toString())
      .withResponseBody(IOUtils.toString(httpResponse.getBody(), Charset.defaultCharset()))
      .withStatusCode(httpResponse.getRawStatusCode())
      .withStatusText(httpResponse.getStatusCode().getReasonPhrase())
      .withRequestHeaders(httpRequest.getHeaders())
      .withResponseHeaders(httpResponse.getHeaders())
      .build();
  }
}
