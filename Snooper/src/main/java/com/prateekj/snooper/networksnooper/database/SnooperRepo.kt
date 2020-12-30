package com.prateekj.snooper.networksnooper.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.prateekj.snooper.database.SnooperDbHelper
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_DATE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_ERROR
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_TYPE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HEADER_VALUE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_HTTP_CALL_RECORD_ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_METHOD
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_PAYLOAD
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_RESPONSE_BODY
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_STATUSCODE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_STATUSTEXT
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.COLUMN_URL
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_TABLE_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_VALUE_TABLE_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_GET_BY_ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_GET_SORT_BY_DATE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_SEARCH
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_TABLE_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_HEADER_GET_BY_CALL_ID
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_HEADER_VALUE_GET_BY_HEADER_ID
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue
import java.util.ArrayList

class SnooperRepo(context: Context) {

  private val dbWriteHelper: SnooperDbHelper = SnooperDbHelper.create(context)
  private val dbReadHelper: SnooperDbHelper = SnooperDbHelper.create(context)

  fun save(httpCallRecord: HttpCallRecord): Long {
    val database = dbWriteHelper.writableDatabase
    val values = ContentValues()
    values.put(COLUMN_URL, httpCallRecord.url)
    values.put(COLUMN_PAYLOAD, httpCallRecord.payload)
    values.put(COLUMN_RESPONSE_BODY, httpCallRecord.responseBody)
    values.put(COLUMN_METHOD, httpCallRecord.method)
    values.put(COLUMN_STATUSCODE, httpCallRecord.statusCode)
    values.put(COLUMN_STATUSTEXT, httpCallRecord.statusText)
    values.put(COLUMN_DATE, httpCallRecord.date!!.time)
    values.put(COLUMN_ERROR, httpCallRecord.error)
    val httpCallRecordId = database.insert(HTTP_CALL_RECORD_TABLE_NAME, null, values)
    try {
      database.beginTransaction()
      saveHeaders(database, httpCallRecordId, httpCallRecord.requestHeaders!!, "req")
      saveHeaders(database, httpCallRecordId, httpCallRecord.responseHeaders!!, "res")
      database.setTransactionSuccessful()
    } finally {
      database.endTransaction()
      database.close()
    }
    return httpCallRecordId
  }

  fun findAllSortByDate(): List<HttpCallRecord> {
    val httpCallRecords = ArrayList<HttpCallRecord>()
    val cursorParser = HttpCallRecordCursorParser()
    val database = dbReadHelper.readableDatabase
    val cursor = database.rawQuery(HTTP_CALL_RECORD_GET_SORT_BY_DATE, null)
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor))
    }
    cursor.close()
    database.close()
    return httpCallRecords
  }

  fun searchHttpRecord(text: String): List<HttpCallRecord> {
    val httpCallRecords = ArrayList<HttpCallRecord>()
    val cursorParser = HttpCallRecordCursorParser()
    val database = dbReadHelper.readableDatabase
    val cursor = database.rawQuery(
      HTTP_CALL_RECORD_SEARCH,
      arrayOf(likeParam(text), likeParam(text), likeParam(text), likeParam(text))
    )
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor))
    }
    cursor.close()
    database.close()
    return httpCallRecords
  }

  private fun likeParam(text: String): String {
    return "%$text%"
  }


  fun findAllSortByDateAfter(id: Long, pageSize: Int): MutableList<HttpCallRecord> {
    val httpCallRecords = ArrayList<HttpCallRecord>()
    val cursorParser = HttpCallRecordCursorParser()
    val database = dbReadHelper.readableDatabase
    val cursor: Cursor
    cursor = if (id == -1L) {
      database.rawQuery(
        HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE,
        arrayOf(pageSize.toString())
      )
    } else {
      database.rawQuery(
        HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE,
        arrayOf(id.toString(), pageSize.toString())
      )
    }
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor))
    }
    cursor.close()
    database.close()
    return httpCallRecords
  }


  fun findById(id: Long): HttpCallRecord {
    val database = dbReadHelper.readableDatabase
    val cursorParser = HttpCallRecordCursorParser()
    val cursor =
      database.rawQuery(HTTP_CALL_RECORD_GET_BY_ID, arrayOf(id.toString()))
    cursor.moveToNext()
    val httpCallRecord = cursorParser.parse(cursor)
    httpCallRecord.requestHeaders = findHeader(database, httpCallRecord.id, "req")
    httpCallRecord.responseHeaders = findHeader(database, httpCallRecord.id, "res")
    database.close()
    return httpCallRecord
  }

  fun deleteAll() {
    val database = dbWriteHelper.writableDatabase
    try {
      database.beginTransaction()
      database.delete(HTTP_CALL_RECORD_TABLE_NAME, null, null)
      database.setTransactionSuccessful()
    } finally {
      database.endTransaction()
      database.close()
    }
  }

  private fun findHeader(
    database: SQLiteDatabase,
    callId: Long,
    headerType: String
  ): List<HttpHeader> {
    val httpHeaders = ArrayList<HttpHeader>()
    val cursorParser = HttpHeaderCursorParser()
    val cursor = database.rawQuery(
      HTTP_HEADER_GET_BY_CALL_ID,
      arrayOf(callId.toString(), headerType)
    )
    while (cursor.moveToNext()) {
      val httpHeader = cursorParser.parse(cursor)
      httpHeader.values = findHeaderValue(database, httpHeader.id)
      httpHeaders.add(httpHeader)
    }
    cursor.close()
    return httpHeaders
  }

  private fun findHeaderValue(database: SQLiteDatabase, headerId: Int): List<HttpHeaderValue> {
    val httpHeaderValues = ArrayList<HttpHeaderValue>()
    val cursorParser = HttpHeaderValueCursorParser()
    val cursor = database.rawQuery(
      HTTP_HEADER_VALUE_GET_BY_HEADER_ID,
      arrayOf(headerId.toString())
    )
    while (cursor.moveToNext()) {
      httpHeaderValues.add(cursorParser.parse(cursor))
    }
    cursor.close()
    return httpHeaderValues
  }

  private fun saveHeaders(
    database: SQLiteDatabase,
    httpCallRecordId: Long,
    requestHeaders: List<HttpHeader>,
    headerType: String
  ) {
    for (httpHeader in requestHeaders) {
      saveHeader(database, httpHeader, httpCallRecordId, headerType)
    }
  }

  private fun saveHeader(
    database: SQLiteDatabase,
    httpHeader: HttpHeader,
    httpCallRecordId: Long,
    headerType: String
  ) {
    val values = ContentValues()
    values.put(COLUMN_HEADER_NAME, httpHeader.name)
    values.put(COLUMN_HEADER_TYPE, headerType)
    values.put(COLUMN_HTTP_CALL_RECORD_ID, httpCallRecordId)
    val headerId = database.insert(HEADER_TABLE_NAME, null, values)
    for (httpHeaderValue in httpHeader.values) {
      saveHeaderValue(database, httpHeaderValue, headerId)
    }
  }

  private fun saveHeaderValue(database: SQLiteDatabase, value: HttpHeaderValue, headerId: Long) {
    val values = ContentValues()
    values.put(COLUMN_HEADER_VALUE, value.value)
    values.put(COLUMN_HEADER_ID, headerId)
    database.insert(HEADER_VALUE_TABLE_NAME, null, values)
  }
}
