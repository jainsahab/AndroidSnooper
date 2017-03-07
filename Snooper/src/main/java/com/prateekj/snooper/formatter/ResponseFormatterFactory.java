package com.prateekj.snooper.formatter;

public class ResponseFormatterFactory {

  public ResponseFormatter getFor(String data) {
    if(isXmlType(data)) {
      return new XmlFormatter();
    }
    return new JsonResponseFormatter();
  }

  private boolean isXmlType(String data) {
    return data.toLowerCase().contains("xml");
  }
}
