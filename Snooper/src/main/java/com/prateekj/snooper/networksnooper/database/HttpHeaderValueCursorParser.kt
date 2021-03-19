package com.prateekj.snooper.networksnooper.database

import android.database.Cursor

import com.prateekj.snooper.database.CursorParser
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue

import android.provider.BaseColumns._ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_VALUE

class HttpHeaderValueCursorParser : CursorParser<HttpHeaderValue> {

  override fun parse(cursor: Cursor): HttpHeaderValue {
    return HttpHeaderValue().apply {
      setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)))
      value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_VALUE))
    }
  }
}
