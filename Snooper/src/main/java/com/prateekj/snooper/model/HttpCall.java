package com.prateekj.snooper.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.prateekj.snooper.model.HttpHeader.from;

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
  private RealmList<HttpHeader> requestHeaders;
  private RealmList<HttpHeader> responseHeaders;


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

  public RealmList<HttpHeader> getRequestHeaders() {
    return requestHeaders;
  }

  public HttpHeader getRequestHeader(final String name) {
    return filterFromCollection(name, getRequestHeaders());
  }

  public RealmList<HttpHeader> getResponseHeaders() {
    return responseHeaders;
  }

  public HttpHeader getResponseHeader(final String name) {
    return filterFromCollection(name, getResponseHeaders());
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

    public Builder withRequestHeaders(Map<String, List<String>> headers) {
      RealmList<HttpHeader> realmList = new RealmList<>();
      realmList.addAll(from(headers));
      httpCall.requestHeaders = realmList;
      return this;
    }

    public Builder withResponseHeaders(Map<String, List<String>> headers) {
      RealmList<HttpHeader> realmList = new RealmList<>();
      realmList.addAll(from(headers));
      httpCall.responseHeaders = realmList;
      return this;
    }
  }
}
