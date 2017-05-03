package org.springframework.http.client;

import com.prateekj.snooper.AndroidSnooper;
import com.prateekj.snooper.networksnooper.model.HttpCall;

import org.springframework.http.HttpRequest;

import java.io.IOException;

public class SnooperInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] byteArray,
                                      ClientHttpRequestExecution execution) throws IOException {

    ClientHttpResponse streamResponse = execution.execute(request, byteArray);
    BufferingClientHttpResponseWrapper httpResponse = new BufferingClientHttpResponseWrapper(streamResponse);
    AndroidSnooper snooper = AndroidSnooper.getInstance();

    HttpCall call = new SpringHttpRequestTransformer().transform(request, byteArray, httpResponse);
    snooper.record(call);
    return httpResponse;
  }
}