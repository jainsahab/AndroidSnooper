package com.prateekj.snooper.model;

import com.google.common.base.Function;

import java.util.List;

import io.realm.RealmObject;

import static com.google.common.collect.Lists.transform;

public class HttpHeaderValue extends RealmObject{
  private String value;

  public HttpHeaderValue() {
  }

  public HttpHeaderValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
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
