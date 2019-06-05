package com.prateekj.snooper.networksnooper.viewmodel;

import androidx.annotation.ColorRes;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Locale.US;

public class HttpCallViewModel {

  private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
  private static final int RANGE_START_HTTP_OK = 200;
  private static final int RANGE_END_HTTP_OK = 299;
  private static final int RANGE_END_HTTP_REDIRECTION = 399;
  private final HttpCallRecord httpCall;

  public HttpCallViewModel(HttpCallRecord httpCall) {
    this.httpCall = httpCall;
  }

  public String getUrl() {
    return httpCall.getUrl();
  }

  public String getMethod() {
    return httpCall.getMethod();
  }

  public String getStatusCode() {
    return String.valueOf(httpCall.getStatusCode());
  }

  public String getStatusText() {
    return httpCall.getStatusText();
  }

  public List<HttpHeader> getRequestHeaders() {
    return httpCall.getRequestHeaders();
  }

  public List<HttpHeader> getResponseHeaders() {
    return httpCall.getResponseHeaders();
  }

  public String getTimeStamp() {
    DateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT, US);
    return df.format(httpCall.getDate());
  }

  public @ColorRes int getStatusColor() {
    int statusCode = httpCall.getStatusCode();

    if (statusCode >= RANGE_START_HTTP_OK && statusCode <= RANGE_END_HTTP_OK) {
      return R.color.snooper_green;
    } else if (statusCode <= RANGE_END_HTTP_REDIRECTION) {
      return R.color.snooper_yellow;
    } else {
      return R.color.snooper_red;
    }
  }

  public int getResponseInfoVisibility() {
    return httpCall.hasError() ? GONE : VISIBLE;
  }

  public int getFailedTextVisibility() {
    return httpCall.hasError() ? VISIBLE : GONE;
  }

  public int getResponseHeaderVisibility() {
    return hasHeaders(httpCall.getResponseHeaders()) ? VISIBLE : GONE;
  }

  public int getRequestHeaderVisibility() {
    return hasHeaders(httpCall.getRequestHeaders()) ? VISIBLE : GONE;
  }

  private boolean hasHeaders(List<HttpHeader> headers) {
    return headers != null && !headers.isEmpty();
  }
}
