package com.prateekj.snooper.networksnooper.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpCall {

  private int id;
  private String url;
  private String payload;
  private String method;
  private String responseBody;
  private String statusText;
  private int statusCode;
  private Date date;
  private List<HttpHeader> requestHeaders;
  private List<HttpHeader> responseHeaders;

  private Map<String, List<String>> rawRequestHeaders = new HashMap<>();
  private Map<String, List<String>> rawResponseHeaders = new HashMap<>();
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

  public List<HttpHeader> getRequestHeaders() {
    return requestHeaders;
  }

  public Map<String, List<String>> getRawRequestHeaders() {
    return rawRequestHeaders;
  }

  public Map<String, List<String>> getRawResponseHeaders() {
    return rawResponseHeaders;
  }

  public HttpHeader getRequestHeader(final String name) {
    return filterFromCollection(name, getRequestHeaders());
  }

  public List<HttpHeader> getResponseHeaders() {
    return responseHeaders;
  }

  public HttpHeader getResponseHeader(final String name) {
    return filterFromCollection(name, getResponseHeaders());
  }

  public String getError() {
    return error;
  }

  public boolean hasError() {
    return getError() != null;
  }

  private HttpHeader filterFromCollection(final String name, List<HttpHeader> collection) {
    Iterator<HttpHeader> iterator = Collections2.filter(collection, new Predicate<HttpHeader>() {
      @Override
      public boolean apply(HttpHeader header) {
        return header.getName().equalsIgnoreCase(name);
      }
    }).iterator();
    return iterator.hasNext() ? iterator.next() : null;
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

    public Builder withRequestHeaders(Map<String, List<String>> headers) {
      List<HttpHeader> realmList = new ArrayList<>();
      realmList.addAll(HttpHeader.from(headers));
      httpCall.rawRequestHeaders = headers;
      httpCall.requestHeaders = realmList;
      return this;
    }

    public Builder withResponseHeaders(Map<String, List<String>> headers) {
      List<HttpHeader> realmList = new ArrayList<>();
      realmList.addAll(HttpHeader.from(headers));
      httpCall.rawResponseHeaders = headers;
      httpCall.responseHeaders = realmList;
      return this;
    }

    public Builder withError(String error) {
      httpCall.error = error;
      return this;
    }
  }
}
