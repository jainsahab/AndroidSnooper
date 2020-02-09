package com.prateekj.snooper.networksnooper.database


import android.provider.BaseColumns

class HttpCallRecordContract : BaseColumns {
  companion object {

    const val HTTP_CALL_RECORD_TABLE_NAME = "http_calls"
    internal const val COLUMN_URL = "url"
    internal const val COLUMN_PAYLOAD = "payload"
    internal const val COLUMN_METHOD = "method"
    internal const val COLUMN_RESPONSE_BODY = "responseBody"
    internal const val COLUMN_STATUSTEXT = "statusText"
    internal const val COLUMN_STATUSCODE = "statusCode"
    internal const val COLUMN_DATE = "date"
    internal const val COLUMN_ERROR = "error"


    const val HEADER_TABLE_NAME = "header"
    internal const val COLUMN_HEADER_NAME = "name"
    internal const val COLUMN_HEADER_TYPE = "type"
    internal const val COLUMN_HTTP_CALL_RECORD_ID = "record_id"

    const val HEADER_VALUE_TABLE_NAME = "header_value"
    internal const val COLUMN_HEADER_VALUE = "value"
    internal const val COLUMN_HEADER_ID = "header_id"

    internal const val HTTP_CALL_RECORD_GET_SORT_BY_DATE =
      "select * from $HTTP_CALL_RECORD_TABLE_NAME order by date DESC"

    internal const val HTTP_CALL_RECORD_SEARCH =
      "select * from $HTTP_CALL_RECORD_TABLE_NAME WHERE $COLUMN_URL LIKE ? OR $COLUMN_PAYLOAD LIKE " +
        "? OR $COLUMN_RESPONSE_BODY LIKE ? OR $COLUMN_ERROR LIKE ? order by date DESC"

    const val HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE =
      "select * from $HTTP_CALL_RECORD_TABLE_NAME order by date DESC LIMIT ?"

    internal const val HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE =
      "select * from $HTTP_CALL_RECORD_TABLE_NAME WHERE ${BaseColumns._ID} < ? order by date DESC LIMIT ?"

    internal const val HTTP_CALL_RECORD_GET_BY_ID =
      "select * from " + HTTP_CALL_RECORD_TABLE_NAME + " WHERE " + BaseColumns._ID + " = ?"

    const val HTTP_HEADER_GET_BY_CALL_ID =
      "select * from $HEADER_TABLE_NAME WHERE $COLUMN_HTTP_CALL_RECORD_ID = ? AND $COLUMN_HEADER_TYPE = ?"

    internal const val HTTP_HEADER_VALUE_GET_BY_HEADER_ID =
      "select * from $HEADER_VALUE_TABLE_NAME WHERE $COLUMN_HEADER_ID = ?"

    const val HTTP_CALL_RECORD_CREATE_TABLE =
      "CREATE TABLE IF NOT EXISTS " + HTTP_CALL_RECORD_TABLE_NAME + " (" +
        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_URL + " TEXT," +
        COLUMN_PAYLOAD + " TEXT," +
        COLUMN_RESPONSE_BODY + " TEXT," +
        COLUMN_ERROR + " TEXT," +
        COLUMN_METHOD + " VARCHAR(10)," +
        COLUMN_STATUSTEXT + " VARCHAR(10)," +
        COLUMN_STATUSCODE + " INTEGER," +
        COLUMN_DATE + " DOUBLE)"

    const val HEADER_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + HEADER_TABLE_NAME + " (" +
      BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_HEADER_TYPE + " VARCHAR(3)," +
      COLUMN_HEADER_NAME + " VARCHAR(255)," +
      COLUMN_HTTP_CALL_RECORD_ID + " INTEGER," +
      "CONSTRAINT chk_header_type CHECK (" + COLUMN_HEADER_TYPE + " IN ('req', 'res'))" +
      "CONSTRAINT fk_http_call_header FOREIGN KEY (" + COLUMN_HTTP_CALL_RECORD_ID + ") REFERENCES " + HTTP_CALL_RECORD_TABLE_NAME + "(" + BaseColumns._ID + ") ON DELETE CASCADE);"

    const val HEADER_VALUE_CREATE_TABLE =
      "CREATE TABLE IF NOT EXISTS " + HEADER_VALUE_TABLE_NAME + " (" +
        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_HEADER_VALUE + " VARCHAR(255)," +
        COLUMN_HEADER_ID + " INTEGER," +
        "CONSTRAINT fk_header_value FOREIGN KEY (" + COLUMN_HEADER_ID + ") REFERENCES " + HEADER_TABLE_NAME + "(" + BaseColumns._ID + ") ON DELETE CASCADE);"
  }
}
