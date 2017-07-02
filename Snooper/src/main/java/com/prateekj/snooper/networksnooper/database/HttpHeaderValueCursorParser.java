package com.prateekj.snooper.networksnooper.database;

import android.database.Cursor;

import com.prateekj.snooper.database.CursorParser;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import static android.provider.BaseColumns._ID;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.COLUMN_HEADER_VALUE;

public class HttpHeaderValueCursorParser implements CursorParser<HttpHeaderValue> {

  @Override
  public HttpHeaderValue parse(Cursor cursor) {
    HttpHeaderValue httpHeaderValue = new HttpHeaderValue();
    httpHeaderValue.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
    httpHeaderValue.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_HEADER_VALUE)));
    return httpHeaderValue;
  }
}
