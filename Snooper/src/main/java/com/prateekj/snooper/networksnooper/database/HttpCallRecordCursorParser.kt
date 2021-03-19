package com.prateekj.snooper.networksnooper.database

import android.database.Cursor

import com.prateekj.snooper.database.CursorParser
import com.prateekj.snooper.networksnooper.model.HttpCallRecord

import java.util.Date

import android.provider.BaseColumns._ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_DATE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_ERROR
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_METHOD
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_PAYLOAD
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_RESPONSE_BODY
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_STATUSCODE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_STATUSTEXT
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_URL

class HttpCallRecordCursorParser : CursorParser<HttpCallRecord> {

  override fun parse(cursor: Cursor): HttpCallRecord {
    return HttpCallRecord().apply {
      id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID))
      url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))
      payload = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYLOAD))
      responseBody = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSE_BODY))
      method = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METHOD))
      statusCode = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUSCODE))
      statusText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUSTEXT))
      date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
      error = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ERROR))
    }
  }
}
