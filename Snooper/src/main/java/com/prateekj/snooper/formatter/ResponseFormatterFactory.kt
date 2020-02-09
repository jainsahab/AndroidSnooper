package com.prateekj.snooper.formatter

class ResponseFormatterFactory {

  fun getFor(data: String): ResponseFormatter {
    return when {
      isXmlType(data) -> XmlFormatter()
      isJsonType(data) -> JsonResponseFormatter()
      else -> PlainTextFormatter()
    }
  }

  private fun isXmlType(data: String): Boolean {
    return data.toLowerCase().contains("xml")
  }

  private fun isJsonType(data: String): Boolean {
    return data.toLowerCase().contains("json")
  }
}
