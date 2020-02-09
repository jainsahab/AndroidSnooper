package com.prateekj.snooper.networksnooper.model

class HttpHeaderValue {
  private var id: Int = 0
  lateinit var value: String

  constructor() {}

  constructor(value: String) {
    this.value = value
  }

  fun setId(id: Int) {
    this.id = id
  }

  companion object {
    fun from(strings: List<String>) = strings.map { HttpHeaderValue(it) }
  }
}
