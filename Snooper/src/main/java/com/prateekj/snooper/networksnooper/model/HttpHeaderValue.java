package com.prateekj.snooper.networksnooper.model;

import com.google.common.base.Function;

import java.util.List;

import static com.google.common.collect.Lists.transform;

public class HttpHeaderValue {
  private int id;
  private String value;

  public HttpHeaderValue() {
  }

  public HttpHeaderValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public static List<HttpHeaderValue> from(List<String> strings) {
    return transform(strings, new Function<String, HttpHeaderValue>() {
      @Override
      public HttpHeaderValue apply(String value) {
        return new HttpHeaderValue(value);
      }
    });
  }
}
