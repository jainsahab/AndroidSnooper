package com.prateekj.snooper;

import io.realm.RealmObject;

public class HttpCall extends RealmObject{

  private String url;
  private String payload;
  private String method;
  private String responseBody;
  private String statusText;
  private int statusCode;


  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public static class HttpCallBuilder {

    private final HttpCall httpCall;

    public HttpCallBuilder() {
      httpCall = new HttpCall();
    }

    public HttpCallBuilder withMethod(String httpMethod) {
      httpCall.method = httpMethod;
      return this;
    }

    public HttpCallBuilder withUrl(String url) {
      httpCall.url = url;
      return this;
    }

    public HttpCall build() {
      return httpCall;
    }

    public HttpCallBuilder withPayload(String payload) {
      httpCall.payload = payload;
      return this;
    }

    public HttpCallBuilder withResponseBody(String responseBody) {
      httpCall.responseBody = responseBody;
      return this;
    }

    public HttpCallBuilder withStatusText(String statusText) {
      httpCall.statusText = statusText;
      return this;
    }

    public HttpCallBuilder withStatusCode(int rawStatusCode) {
      httpCall.statusCode = rawStatusCode;
      return this;
    }
  }
}
