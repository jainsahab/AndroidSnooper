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
    val httpCallRecord = HttpCallRecord()
    httpCallRecord.id = cursor.getLong(cursor.getColumnIndex(_ID))
    httpCallRecord.url = cursor.getString(cursor.getColumnIndex(COLUMN_URL))
    httpCallRecord.payload = cursor.getString(cursor.getColumnIndex(COLUMN_PAYLOAD))
    httpCallRecord.responseBody = cursor.getString(cursor.getColumnIndex(COLUMN_RESPONSE_BODY))
    httpCallRecord.method = cursor.getString(cursor.getColumnIndex(COLUMN_METHOD))
    httpCallRecord.statusCode = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUSCODE))
    httpCallRecord.statusText = cursor.getString(cursor.getColumnIndex(COLUMN_STATUSTEXT))
    httpCallRecord.date = Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)))
    httpCallRecord.error = cursor.getString(cursor.getColumnIndex(COLUMN_ERROR))
    return httpCallRecord
  }
}
