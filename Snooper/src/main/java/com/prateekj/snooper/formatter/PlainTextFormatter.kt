package com.prateekj.snooper.formatter

class PlainTextFormatter : ResponseFormatter {
  override fun format(response: String): String {
    return response.replace("\r".toRegex(), System.getProperty("line.separator"))
  }
}
