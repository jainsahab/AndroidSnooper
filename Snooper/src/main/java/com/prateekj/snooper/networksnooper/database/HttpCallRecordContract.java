package com.prateekj.snooper.networksnooper.database;


import android.provider.BaseColumns;

public class HttpCallRecordContract implements BaseColumns {

  public static final String HTTP_CALL_RECORD_TABLE_NAME = "http_calls";
  static final String COLUMN_URL = "url";
  static final String COLUMN_PAYLOAD = "payload";
  static final String COLUMN_METHOD = "method";
  static final String COLUMN_RESPONSE_BODY = "responseBody";
  static final String COLUMN_STATUSTEXT = "statusText";
  static final String COLUMN_STATUSCODE = "statusCode";
  static final String COLUMN_DATE = "date";
  static final String COLUMN_ERROR = "error";


  public static final String HEADER_TABLE_NAME = "header";
  static final String COLUMN_HEADER_NAME = "name";
  static final String COLUMN_HEADER_TYPE = "type";
  static final String COLUMN_HTTP_CALL_RECORD_ID = "record_id";

  public static final String HEADER_VALUE_TABLE_NAME = "header_value";
  static final String COLUMN_HEADER_VALUE = "value";
  static final String COLUMN_HEADER_ID = "header_id";

  static final String HTTP_CALL_RECORD_GET_SORT_BY_DATE =
    "select * from " + HTTP_CALL_RECORD_TABLE_NAME + " order by date DESC";

  static final String HTTP_CALL_RECORD_SEARCH =
    String.format("select * from %s WHERE %s LIKE ? OR %s LIKE ? OR %s LIKE ? OR %s LIKE ? order by date DESC",
      HTTP_CALL_RECORD_TABLE_NAME,
      COLUMN_URL,
      COLUMN_PAYLOAD,
      COLUMN_RESPONSE_BODY,
      COLUMN_ERROR);

  static final String HTTP_CALL_RECORD_GET_SORT_BY_DATE_WITH_SIZE =
    String.format("select * from %s order by date DESC LIMIT ?", HTTP_CALL_RECORD_TABLE_NAME);

  static final String HTTP_CALL_RECORD_GET_NEXT_SORT_BY_DATE_WITH_SIZE =
    String.format("select * from %s WHERE %s < ? order by date DESC LIMIT ?", HTTP_CALL_RECORD_TABLE_NAME, _ID);

  static final String HTTP_CALL_RECORD_GET_BY_ID =
    "select * from " + HTTP_CALL_RECORD_TABLE_NAME + " WHERE " + _ID + " = ?";

  static final String HTTP_HEADER_GET_BY_CALL_ID =
    String.format("select * from %s WHERE %s = ? AND %s = ?", HEADER_TABLE_NAME, COLUMN_HTTP_CALL_RECORD_ID, COLUMN_HEADER_TYPE);

  static final String HTTP_HEADER_VALUE_GET_BY_HEADER_ID =
    String.format("select * from %s WHERE %s = ?", HEADER_VALUE_TABLE_NAME, COLUMN_HEADER_ID);

  public static final String HTTP_CALL_RECORD_CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS " + HTTP_CALL_RECORD_TABLE_NAME + " (" +
      _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_URL + " TEXT," +
      COLUMN_PAYLOAD + " TEXT," +
      COLUMN_RESPONSE_BODY + " TEXT," +
      COLUMN_ERROR + " TEXT," +
      COLUMN_METHOD + " VARCHAR(10)," +
      COLUMN_STATUSTEXT + " VARCHAR(10)," +
      COLUMN_STATUSCODE + " INTEGER," +
      COLUMN_DATE + " DOUBLE)";

  public static final String HEADER_CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS " + HEADER_TABLE_NAME + " (" +
      _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_HEADER_TYPE + " VARCHAR(3)," +
      COLUMN_HEADER_NAME + " VARCHAR(255)," +
      COLUMN_HTTP_CALL_RECORD_ID + " INTEGER," +
      "CONSTRAINT chk_header_type CHECK (" + COLUMN_HEADER_TYPE + " IN ('req', 'res'))" +
      "CONSTRAINT fk_http_call_header FOREIGN KEY (" + COLUMN_HTTP_CALL_RECORD_ID + ") REFERENCES " + HTTP_CALL_RECORD_TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE);";

  public static final String HEADER_VALUE_CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS " + HEADER_VALUE_TABLE_NAME + " (" +
      _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_HEADER_VALUE + " VARCHAR(255)," +
      COLUMN_HEADER_ID + " INTEGER," +
      "CONSTRAINT fk_header_value FOREIGN KEY (" + COLUMN_HEADER_ID + ") REFERENCES " + HEADER_TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE);";
}
