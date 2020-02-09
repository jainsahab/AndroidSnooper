package com.prateekj.snooper.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.AnyThread
import androidx.annotation.MainThread

import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_CREATE_TABLE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_VALUE_CREATE_TABLE
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_CREATE_TABLE

class SnooperDbHelper private constructor(context: Context) :
  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  init {
    super.setWriteAheadLoggingEnabled(true)
  }

  override fun onConfigure(db: SQLiteDatabase) {
    db.execSQL("PRAGMA foreign_keys = ON")
  }

  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(HTTP_CALL_RECORD_CREATE_TABLE)
    db.execSQL(HEADER_CREATE_TABLE)
    db.execSQL(HEADER_VALUE_CREATE_TABLE)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

  override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
    if (!enabled) {
      throw UnsupportedOperationException("Write ahead logging is required.")
    }
  }

  companion object {

    private const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "snooper.db"
    private var INSTANCE: SnooperDbHelper? = null

    @MainThread
    fun getInstance(context: Context): SnooperDbHelper {
      return INSTANCE ?: SnooperDbHelper(context).also {
        INSTANCE = it
      }
    }

    @AnyThread
    fun create(context: Context): SnooperDbHelper {
      return SnooperDbHelper(context)
    }
  }
}
