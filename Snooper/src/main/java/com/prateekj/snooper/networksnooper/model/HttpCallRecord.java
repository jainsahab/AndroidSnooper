package com.prateekj.snooper.networksnooper.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HttpCallRecord {
  private long id;
  private String url;
  private String payload;
  private String method;
  private String responseBody;
  private String statusText;
  private int statusCode;
  private Date date;
  private String error;
  private List<HttpHeader> requestHeaders;
  private List<HttpHeader> responseHeaders;

  public long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public String getPayload() {
    return payload;
  }

  public String getMethod() {
    return method;
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

  public List<HttpHeader> getResponseHeaders() {
    return responseHeaders;
  }

  public String getError() {
    return error;
  }

  public HttpHeader getResponseHeader(final String name) {
    return filterFromCollection(name, getResponseHeaders());
  }

  public HttpHeader getRequestHeader(final String name) {
    return filterFromCollection(name, getRequestHeaders());
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

  public boolean hasError() {
    return getError() != null;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setResponseBody(String responseBody) {
    this.responseBody = responseBody;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setRequestHeaders(List<HttpHeader> requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  public void setResponseHeaders(List<HttpHeader> responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  public static HttpCallRecord from(HttpCall httpCall) {
    HttpCallRecord httpCallRecord = new HttpCallRecord();
    httpCallRecord.url = httpCall.getUrl();
    httpCallRecord.payload = httpCall.getPayload();
    httpCallRecord.method = httpCall.getMethod();
    httpCallRecord.responseBody = httpCall.getResponseBody();
    httpCallRecord.statusText = httpCall.getStatusText();
    httpCallRecord.statusCode = httpCall.getStatusCode();
    httpCallRecord.date = httpCall.getDate();
    httpCallRecord.error = httpCall.getError();
    httpCallRecord.requestHeaders = HttpHeader.from(httpCall.getRawRequestHeaders());
    httpCallRecord.responseHeaders = HttpHeader.from(httpCall.getRawResponseHeaders());
    return httpCallRecord;
  }
}
