package com.prateekj.snooper.networksnooper.database;

import android.database.Cursor;

import com.prateekj.snooper.database.CursorParser;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;

import java.util.Date;

import static android.provider.BaseColumns._ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_DATE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_ERROR;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_METHOD;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_PAYLOAD;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_RESPONSE_BODY;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_STATUSCODE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_STATUSTEXT;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_URL;

public class HttpCallRecordCursorParser implements CursorParser<HttpCallRecord> {

  @Override
  public HttpCallRecord parse(Cursor cursor) {
    HttpCallRecord httpCallRecord = new HttpCallRecord();
    httpCallRecord.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
    httpCallRecord.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
    httpCallRecord.setPayload(cursor.getString(cursor.getColumnIndex(COLUMN_PAYLOAD)));
    httpCallRecord.setResponseBody(cursor.getString(cursor.getColumnIndex(COLUMN_RESPONSE_BODY)));
    httpCallRecord.setMethod(cursor.getString(cursor.getColumnIndex(COLUMN_METHOD)));
    httpCallRecord.setStatusCode(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUSCODE)));
    httpCallRecord.setStatusText(cursor.getString(cursor.getColumnIndex(COLUMN_STATUSTEXT)));
    httpCallRecord.setDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))));
    httpCallRecord.setError(cursor.getString(cursor.getColumnIndex(COLUMN_ERROR)));
    return httpCallRecord;
  }
}
