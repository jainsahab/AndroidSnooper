package com.prateekj.snooper.networksnooper.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCall {

  public String getPayload() {
    return "";
  }

  public String getMethod() {
    return "";
  }

  public String getUrl() {
    return "";
  }

  public String getResponseBody() {
    return "";
  }

  public String getStatusText() {
    return "";
  }

  public int getStatusCode() {
    return -1;
  }

  public Date getDate() {
    return new Date();
  }

  public Map<String, List<String>> getRequestHeaders() {
    return new HashMap<>();
  }

  public Map<String, List<String>> getResponseHeaders() {
    return new HashMap<>();
  }

  public String getError() {
    return "";
  }

  public void setDate(Date date) {
    ;
  }

  public static class Builder {

    private final HttpCall httpCall;

    public Builder() {
      httpCall = new HttpCall();
    }

    public Builder withMethod(String httpMethod) {
      return this;
    }

    public Builder withUrl(String url) {
      return this;
    }

    public HttpCall build() {
      return httpCall;
    }

    public Builder withPayload(String payload) {
      return this;
    }

    public Builder withResponseBody(String responseBody) {
      return this;
    }

    public Builder withStatusText(String statusText) {
      return this;
    }

    public Builder withStatusCode(int rawStatusCode) {
      return this;
    }

    public Builder withRequestHeaders(Map<String, List<String>> headers) {
      return this;
    }

    public Builder withResponseHeaders(Map<String, List<String>> headers) {
      return this;
    }

    public Builder withError(String error) {
      return this;
    }
  }
}
