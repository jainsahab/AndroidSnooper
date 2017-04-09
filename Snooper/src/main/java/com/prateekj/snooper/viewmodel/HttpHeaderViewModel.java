package com.prateekj.snooper.viewmodel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Collections2.transform;

public class HttpHeaderViewModel {
  private HttpHeader httpHeader;
  private int headerId;

  public HttpHeaderViewModel(HttpHeader httpHeader, int headerId) {
    this.httpHeader = httpHeader;
    this.headerId = headerId;

  }

  public String headerName() {
    return httpHeader.getName();
  }

  public String headerValues() {
    return StringUtils.join(toHeaderValues(), ";");
  }

  public int getHeaderId() {
    return headerId;
  }

  private Iterator<String> toHeaderValues() {
    return transform(httpHeader.getValues(), new Function<HttpHeaderValue, String>() {
      @Override
      public String apply(HttpHeaderValue headerValue) {
        return  headerValue.getValue();
      }
    }).iterator();
  }

  public static List<HttpHeaderViewModel> toViewModels(List<HttpHeader> headers, final int headerId) {
    return Lists.transform(headers, new Function<HttpHeader, HttpHeaderViewModel>() {
      @Override
      public HttpHeaderViewModel apply(HttpHeader httpHeader) {
        return new HttpHeaderViewModel(httpHeader, headerId);
      }
    });
  }
}
