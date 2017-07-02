package com.prateekj.snooper.networksnooper.model;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

public class HttpHeader {

  public static final String CONTENT_TYPE = "Content-Type";

  private int id;
  private String name;
  private List<HttpHeaderValue> values;

  public HttpHeader() {
  }

  public HttpHeader(String name) {
    this.name = name;
  }

  public void setValues(List<HttpHeaderValue> values) {
    List<HttpHeaderValue> headerValues = new ArrayList<>();
    headerValues.addAll(values);
    this.values = headerValues;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<HttpHeaderValue> getValues() {
    return values;
  }

  public static List<HttpHeader> from(final Map<String, List<String>> headers) {
    return transform(newArrayList(headers.keySet()), new Function<String, HttpHeader>() {
      @Override
      public HttpHeader apply(String headerName) {
        HttpHeader httpHeader = new HttpHeader(headerName);
        httpHeader.setValues(HttpHeaderValue.from(headers.get(headerName)));
        return httpHeader;
      }
    });
  }
}
