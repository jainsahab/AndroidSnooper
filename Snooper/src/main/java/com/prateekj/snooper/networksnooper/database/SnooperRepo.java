package com.prateekj.snooper.networksnooper.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.prateekj.snooper.database.SnooperDbHelper;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import java.util.ArrayList;
import java.util.List;

import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_DATE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_ERROR;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_TYPE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_VALUE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HTTP_CALL_RECORD_ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_METHOD;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_PAYLOAD;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_RESPONSE_BODY;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_STATUSCODE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_STATUSTEXT;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_URL;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_TABLE_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_VALUE_TABLE_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_GET_BY_ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_GET_SORT_BY_DATE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_SEARCH;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_TABLE_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_HEADER_GET_BY_CALL_ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_HEADER_VALUE_GET_BY_HEADER_ID;
import static java.lang.String.valueOf;

public class SnooperRepo {

  private final SnooperDbHelper dbWriteHelper;
  private final SnooperDbHelper dbReadHelper;

  public SnooperRepo(Context context) {
    dbWriteHelper = SnooperDbHelper.create(context);
    dbReadHelper = SnooperDbHelper.create(context);
  }

  public long save(HttpCallRecord httpCallRecord) {
    SQLiteDatabase database = dbWriteHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_URL, httpCallRecord.getUrl());
    values.put(COLUMN_PAYLOAD, httpCallRecord.getPayload());
    values.put(COLUMN_RESPONSE_BODY, httpCallRecord.getResponseBody());
    values.put(COLUMN_METHOD, httpCallRecord.getMethod());
    values.put(COLUMN_STATUSCODE, httpCallRecord.getStatusCode());
    values.put(COLUMN_STATUSTEXT, httpCallRecord.getStatusText());
    values.put(COLUMN_DATE, httpCallRecord.getDate().getTime());
    values.put(COLUMN_ERROR, httpCallRecord.getError());
    long httpCallRecordId = database.insert(HTTP_CALL_RECORD_TABLE_NAME, null, values);
    try {
      database.beginTransaction();
      saveHeaders(database, httpCallRecordId, httpCallRecord.getRequestHeaders(), "req");
      saveHeaders(database, httpCallRecordId, httpCallRecord.getResponseHeaders(), "res");
      database.setTransactionSuccessful();
    }
    finally {
      database.endTransaction();
      database.close();
    }
    return httpCallRecordId;
  }

  public List<HttpCallRecord> findAllSortByDate() {
    ArrayList<HttpCallRecord> httpCallRecords = new ArrayList<>();
    HttpCallRecordCursorParser cursorParser = new HttpCallRecordCursorParser();
    SQLiteDatabase database = dbReadHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery(HTTP_CALL_RECORD_GET_SORT_BY_DATE, null);
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor));
    }
    cursor.close();
    database.close();
    return httpCallRecords;
  }

  public List<HttpCallRecord> searchHttpRecord(String text) {
    ArrayList<HttpCallRecord> httpCallRecords = new ArrayList<>();
    HttpCallRecordCursorParser cursorParser = new HttpCallRecordCursorParser();
    SQLiteDatabase database = dbReadHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery(HTTP_CALL_RECORD_SEARCH, new String[]{likeParam(text), likeParam(text), likeParam(text), likeParam(text)});
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor));
    }
    cursor.close();
    database.close();
    return httpCallRecords;
  }

  @NonNull
  private String likeParam(String text) {
    return "%" + text + "%";
  }


  public List<HttpCallRecord> findAllSortByDateAfter(long id, int pageSize) {
    ArrayList<HttpCallRecord> httpCallRecords = new ArrayList<>();
    HttpCallRecordCursorParser cursorParser = new HttpCallRecordCursorParser();
    SQLiteDatabase database = dbReadHelper.getReadableDatabase();
    Cursor cursor;
    if (id == -1) {
      cursor = database.rawQuery(HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE, new String[]{valueOf(pageSize)});
    } else {
      cursor = database.rawQuery(HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE, new String[]{valueOf(id), valueOf(pageSize)});
    }
    while (cursor.moveToNext()) {
      httpCallRecords.add(cursorParser.parse(cursor));
    }
    cursor.close();
    database.close();
    return httpCallRecords;
  }


  public HttpCallRecord findById(long id) {
    SQLiteDatabase database = dbReadHelper.getReadableDatabase();
    HttpCallRecordCursorParser cursorParser = new HttpCallRecordCursorParser();
    Cursor cursor = database.rawQuery(HTTP_CALL_RECORD_GET_BY_ID, new String[]{Long.toString(id)});
    cursor.moveToNext();
    HttpCallRecord httpCallRecord = cursorParser.parse(cursor);
    httpCallRecord.setRequestHeaders(findHeader(database, httpCallRecord.getId(), "req"));
    httpCallRecord.setResponseHeaders(findHeader(database, httpCallRecord.getId(), "res"));
    database.close();
    return httpCallRecord;
  }

  public void deleteAll() {
    SQLiteDatabase database = dbWriteHelper.getWritableDatabase();
    try {
      database.beginTransaction();
      database.delete(HTTP_CALL_RECORD_TABLE_NAME, null, null);
      database.setTransactionSuccessful();
    }
    finally {
      database.endTransaction();
      database.close();
    }
  }

  private List<HttpHeader> findHeader(SQLiteDatabase database, long callId, String headerType) {
    ArrayList<HttpHeader> httpHeaders = new ArrayList<>();
    HttpHeaderCursorParser cursorParser = new HttpHeaderCursorParser();
    Cursor cursor = database.rawQuery(HTTP_HEADER_GET_BY_CALL_ID, new String[]{Long.toString(callId), headerType});
    while (cursor.moveToNext()) {
      HttpHeader httpHeader = cursorParser.parse(cursor);
      httpHeader.setValues(findHeaderValue(database, httpHeader.getId()));
      httpHeaders.add(httpHeader);
    }
    cursor.close();
    return httpHeaders;
  }

  private List<HttpHeaderValue> findHeaderValue(SQLiteDatabase database, int headerId) {
    ArrayList<HttpHeaderValue> httpHeaderValues = new ArrayList<>();
    HttpHeaderValueCursorParser cursorParser = new HttpHeaderValueCursorParser();
    Cursor cursor = database.rawQuery(HTTP_HEADER_VALUE_GET_BY_HEADER_ID, new String[]{Integer.toString(headerId)});
    while (cursor.moveToNext()) {
      httpHeaderValues.add(cursorParser.parse(cursor));
    }
    cursor.close();
    return httpHeaderValues;
  }

  private void saveHeaders(SQLiteDatabase database, long httpCallRecordId, List<HttpHeader> requestHeaders, String headerType) {
    for (HttpHeader httpHeader : requestHeaders) {
      saveHeader(database, httpHeader, httpCallRecordId, headerType);
    }
  }

  private void saveHeader(SQLiteDatabase database, HttpHeader httpHeader, long httpCallRecordId, String headerType) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_HEADER_NAME, httpHeader.getName());
    values.put(COLUMN_HEADER_TYPE, headerType);
    values.put(COLUMN_HTTP_CALL_RECORD_ID, httpCallRecordId);
    long headerId = database.insert(HEADER_TABLE_NAME, null, values);
    for (HttpHeaderValue httpHeaderValue : httpHeader.getValues()) {
      saveHeaderValue(database, httpHeaderValue, headerId);
    }
  }

  private void saveHeaderValue(SQLiteDatabase database, HttpHeaderValue value, long headerId) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_HEADER_VALUE, value.getValue());
    values.put(COLUMN_HEADER_ID, headerId);
    database.insert(HEADER_VALUE_TABLE_NAME, null, values);
  }
}
