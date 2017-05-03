package com.prateekj.snooper.networksnooper.model;

import com.google.common.base.Function;

import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

public class HttpHeader extends RealmObject {

  public static final String CONTENT_TYPE = "Content-Type";

  private String name;
  private RealmList<HttpHeaderValue> values;

  public HttpHeader() {
  }

  public HttpHeader(String name) {
    this.name = name;
  }

  public void setValues(List<HttpHeaderValue> values) {
    RealmList<HttpHeaderValue> headerValues = new RealmList<>();
    headerValues.addAll(values);
    this.values = headerValues;
  }

  public String getName() {
    return name;
  }

  public RealmList<HttpHeaderValue> getValues() {
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
