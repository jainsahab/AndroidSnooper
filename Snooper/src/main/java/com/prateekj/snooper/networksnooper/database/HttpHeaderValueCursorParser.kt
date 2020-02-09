package com.prateekj.snooper.networksnooper.database

import android.database.Cursor

import com.prateekj.snooper.database.CursorParser
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue

import android.provider.BaseColumns._ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_VALUE

class HttpHeaderValueCursorParser : CursorParser<HttpHeaderValue> {

  override fun parse(cursor: Cursor): HttpHeaderValue {
    val httpHeaderValue = HttpHeaderValue()
    httpHeaderValue.setId(cursor.getInt(cursor.getColumnIndex(_ID)))
    httpHeaderValue.value = cursor.getString(cursor.getColumnIndex(COLUMN_HEADER_VALUE))
    return httpHeaderValue
  }
}
