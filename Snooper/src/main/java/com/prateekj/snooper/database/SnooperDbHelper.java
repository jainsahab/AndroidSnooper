package com.prateekj.snooper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;

import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_CREATE_TABLE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_VALUE_CREATE_TABLE;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_CREATE_TABLE;

public class SnooperDbHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "snooper.db";
  private static SnooperDbHelper mInstance;

  private SnooperDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    super.setWriteAheadLoggingEnabled(true);
  }

  @Override
  public void onConfigure(SQLiteDatabase db) {
    db.execSQL("PRAGMA foreign_keys = ON");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(HTTP_CALL_RECORD_CREATE_TABLE);
    db.execSQL(HEADER_CREATE_TABLE);
    db.execSQL(HEADER_VALUE_CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  @Override
  public void setWriteAheadLoggingEnabled(boolean enabled) {
    if(!enabled) {
      throw new UnsupportedOperationException("Write ahead logging is required.");
    }
  }

  @MainThread
  public static SnooperDbHelper getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new SnooperDbHelper(context);
    }
    return mInstance;
  }

  @AnyThread
  public static SnooperDbHelper create(Context context) {
    return new SnooperDbHelper(context);
  }
}
