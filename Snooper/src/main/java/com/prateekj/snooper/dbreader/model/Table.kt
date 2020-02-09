package com.prateekj.snooper.dbreader.model

class Table(
  var name: String? = null,
  var columns: List<String>? = null,
  var rows: List<Row>? = null
)