package com.prateekj.snooper.viewmodel;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.model.HttpCall;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;

public class HttpBodyViewModel {

  private HttpCall httpCall;
  private ResponseFormatter formatter;
  private int mode;

  public void init(HttpCall httpCall, ResponseFormatter formatter, int mode) {
    this.httpCall = httpCall;
    this.formatter = formatter;
    this.mode = mode;
  }

  public String formattedBody() {
    if (mode == REQUEST_MODE) {
      return this.formatter.format(this.httpCall.getPayload());
    }
    return this.formatter.format(this.httpCall.getResponseBody());
  }
}
