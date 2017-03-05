package com.prateekj.snooper.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HttpCall extends RealmObject implements IncrementalIdModel {

  @PrimaryKey
  private int id;
  private String url;
  private String payload;
  private String method;
  private String responseBody;
  private String statusText;
  private int statusCode;
  private Date date;


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

  @Override
  public Class getClazz() {
    return HttpCall.class;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getId() {
    return id;
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
  }
}
