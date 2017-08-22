package com.prateekj.snooper.networksnooper.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCall {

  private String url;
  private String payload;
  private String method;
  private String responseBody;
  private String statusText;
  private int statusCode;
  private Date date;
  private Map<String, List<String>> requestHeaders = new HashMap<>();
  private Map<String, List<String>> responseHeaders = new HashMap<>();
  private String error;

  public HttpCall() {
    this.date = new Date();
  }

  public String getPayload() {
    return payload;
  }

  public String getMethod() {
    return method;
  }

  public String getUrl() {
    return url;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public String getStatusText() {
    return statusText;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Date getDate() {
    return date;
  }

  public Map<String, List<String>> getRequestHeaders() {
    return requestHeaders;
  }

  public Map<String, List<String>> getResponseHeaders() {
    return responseHeaders;
  }

  public String getError() {
    return error;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public static class Builder {

    private final HttpCall httpCall;

    public Builder() {
      httpCall = new HttpCall();
    }

    public Builder withMethod(String httpMethod) {
      httpCall.method = httpMethod;
      return this;
    }

    public Builder withUrl(String url) {
      httpCall.url = url;
      return this;
    }

    public HttpCall build() {
      return httpCall;
    }

    public Builder withPayload(String payload) {
      httpCall.payload = payload;
      return this;
    }

    public Builder withResponseBody(String responseBody) {
      httpCall.responseBody = responseBody;
      return this;
    }

    public Builder withStatusText(String statusText) {
      httpCall.statusText = statusText;
      return this;
    }

    public Builder withStatusCode(int rawStatusCode) {
      httpCall.statusCode = rawStatusCode;
      return this;
    }

    public Builder withRequestHeaders(Map<String, List<String>> headers) {
      httpCall.requestHeaders = headers;
      return this;
    }

    public Builder withResponseHeaders(Map<String, List<String>> headers) {
      httpCall.responseHeaders = headers;
      return this;
    }

    public Builder withError(String error) {
      httpCall.error = error;
      return this;
    }
  }
}
