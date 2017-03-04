package com.prateekj.snooper.viewmodel;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.model.HttpCall;

public class ResponseBodyViewModel {

  private HttpCall httpCall;
  private ResponseFormatter formatter;

  public void init(HttpCall httpCall, ResponseFormatter formatter) {
    this.httpCall = httpCall;
    this.formatter = formatter;
  }

  public String formattedResponse() {
    return this.formatter.format(this.httpCall.getResponseBody());
  }
}
