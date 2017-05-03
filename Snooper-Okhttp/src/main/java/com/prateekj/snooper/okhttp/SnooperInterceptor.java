package com.prateekj.snooper.okhttp;

import android.util.Log;

import com.prateekj.snooper.AndroidSnooper;
import com.prateekj.snooper.networksnooper.model.HttpCall;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import static okhttp3.ResponseBody.create;

public class SnooperInterceptor implements Interceptor{

  public static final String TAG = SnooperInterceptor.class.getSimpleName();

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    String responseBody = response.body().string();

    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(request.url().toString())
      .withPayload(getRequestBody(request))
      .withMethod(request.method())
      .withResponseBody(responseBody)
      .withStatusCode(response.code())
      .withStatusText(response.message())
      .withRequestHeaders(headers(request.headers()))
      .withResponseHeaders(headers(response.headers()))
      .build();
    AndroidSnooper.getInstance().record(httpCall);

    return response.newBuilder().body(create(response.body().contentType(), responseBody)).build();
  }

  private Map<String, List<String>> headers(Headers headers) {
    HashMap<String, List<String>> extractedHeaders = new HashMap<>();
    for (String headerName : headers.names()) {
      extractedHeaders.put(headerName, headers.values(headerName));
    }
    return  extractedHeaders;
  }

  private String getRequestBody(Request request) {
    try {
      final Request copy = request.newBuilder().build();
      final Buffer buffer = new Buffer();
      if(copy.body() == null) {
        return "";
      }
      copy.body().writeTo(buffer);
      return buffer.readUtf8();
    } catch (final IOException e) {
      Log.d(TAG, "couldn't retrieve request body", e);
      return "";
    }
  }
}
