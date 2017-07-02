package com.prateekj.snooper.networksnooper.database;

import android.database.Cursor;

import com.prateekj.snooper.database.CursorParser;
import com.prateekj.snooper.networksnooper.model.HttpHeader;

import static android.provider.BaseColumns._ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_NAME;

public class HttpHeaderCursorParser implements CursorParser<HttpHeader> {

  @Override
  public HttpHeader parse(Cursor cursor) {
    HttpHeader httpHeader = new HttpHeader();
    httpHeader.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
    httpHeader.setName(cursor.getString(cursor.getColumnIndex(COLUMN_HEADER_NAME)));
    return httpHeader;
  }
}
