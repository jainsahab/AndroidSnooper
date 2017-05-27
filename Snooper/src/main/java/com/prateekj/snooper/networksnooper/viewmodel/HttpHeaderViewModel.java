package com.prateekj.snooper.networksnooper.viewmodel;

import com.google.common.base.Function;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

import static com.google.common.collect.Collections2.transform;

public class HttpHeaderViewModel {
  private HttpHeader httpHeader;

  public HttpHeaderViewModel(HttpHeader httpHeader) {
    this.httpHeader = httpHeader;
  }

  public String headerName() {
    return httpHeader.getName();
  }

  public String headerValues() {
    return StringUtils.join(toHeaderValues(), ";");
  }

  private Iterator<String> toHeaderValues() {
    return transform(httpHeader.getValues(), new Function<HttpHeaderValue, String>() {
      @Override
      public String apply(HttpHeaderValue headerValue) {
        return  headerValue.getValue();
      }
    }).iterator();
  }
}
