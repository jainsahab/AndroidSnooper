package com.prateekj.snooper.networksnooper.database

import android.database.Cursor

import com.prateekj.snooper.database.CursorParser
import com.prateekj.snooper.networksnooper.model.HttpHeader

import android.provider.BaseColumns._ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_NAME

class HttpHeaderCursorParser : CursorParser<HttpHeader> {

  override fun parse(cursor: Cursor): HttpHeader {
    return HttpHeader().apply {
      id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
      name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_NAME))
    }
  }
}
