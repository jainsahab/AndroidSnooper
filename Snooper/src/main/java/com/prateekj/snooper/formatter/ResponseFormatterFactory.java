package com.prateekj.snooper.formatter;

public class ResponseFormatterFactory {

  public ResponseFormatter getFor(String data) {
    if(isXmlType(data)) {
      return new XmlFormatter();
    }
    if (isJsonType(data)) {
      return new JsonResponseFormatter();
    }
    return new PlainTextFormatter();
  }

  private boolean isXmlType(String data) {
    return data.toLowerCase().contains("xml");
  }
  private boolean isJsonType(String data) {
    return data.toLowerCase().contains("json");
  }
}
