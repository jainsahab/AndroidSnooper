package com.prateekj.snooper.transformer;

import com.prateekj.snooper.HttpCall;
import com.prateekj.snooper.HttpCall.HttpCallBuilder;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class SpringHttpRequestTransformer {

  public HttpCall transform(HttpRequest httpRequest, byte[] requestPayload, ClientHttpResponse httpResponse) throws IOException {
    return new HttpCallBuilder()
        .withUrl(httpRequest.getURI().toString())
        .withPayload(new String(requestPayload))
        .withMethod(httpRequest.getMethod().toString())
        .withResponseBody(IOUtils.toString(httpResponse.getBody()))
        .withStatusCode(httpResponse.getRawStatusCode())
        .withStatusText(httpResponse.getStatusCode().getReasonPhrase())
        .build();
  }
}
