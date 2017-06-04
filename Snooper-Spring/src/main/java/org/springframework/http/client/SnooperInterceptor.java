package org.springframework.http.client;

import com.prateekj.snooper.AndroidSnooper;
import com.prateekj.snooper.networksnooper.model.HttpCall;

import org.springframework.http.HttpRequest;

import java.io.IOException;

public class SnooperInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] byteArray,
                                      ClientHttpRequestExecution execution) throws IOException {

    SpringHttpRequestTransformer transformer = new SpringHttpRequestTransformer();
    AndroidSnooper snooper = AndroidSnooper.getInstance();
    ClientHttpResponse streamResponse;
    try {
      streamResponse = execution.execute(request, byteArray);
    } catch (Exception e) {
      HttpCall call = transformer.transform(request, byteArray, e);
      snooper.record(call);
      throw e;
    }
    BufferingClientHttpResponseWrapper httpResponse = new BufferingClientHttpResponseWrapper(streamResponse);
    HttpCall call = transformer.transform(request, byteArray, httpResponse);
    snooper.record(call);
    return httpResponse;
  }
}