package com.prateekj.snooper.networksnooper.database

import android.database.Cursor

import com.prateekj.snooper.database.CursorParser
import com.prateekj.snooper.networksnooper.model.HttpHeader

import android.provider.BaseColumns._ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_NAME

class HttpHeaderCursorParser : CursorParser<HttpHeader> {

  override fun parse(cursor: Cursor): HttpHeader {
    val httpHeader = HttpHeader()
    httpHeader.id = cursor.getInt(cursor.getColumnIndex(_ID))
    httpHeader.name = cursor.getString(cursor.getColumnIndex(COLUMN_HEADER_NAME))
    return httpHeader
  }
}
