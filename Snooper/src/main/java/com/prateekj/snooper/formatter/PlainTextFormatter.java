package com.prateekj.snooper.formatter;

public class PlainTextFormatter implements ResponseFormatter{
  @Override
  public String format(String response) {
    return response;
  }
}
