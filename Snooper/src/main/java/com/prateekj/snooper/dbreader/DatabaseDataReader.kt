package com.prateekj.snooper.dbreader

import android.database.sqlite.SQLiteDatabase
import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.model.Row
import com.prateekj.snooper.dbreader.model.Table
import java.util.ArrayList
import java.util.Arrays

class DatabaseDataReader {

  fun getData(database: SQLiteDatabase): Database {
    val databaseData = Database().also {
      it.path = database.path
      it.version = database.version
    }
    val tableNameCursor =
      database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
    val tables = ArrayList<String>()
    tableNameCursor.use { cursor ->
      if (cursor != null && cursor.moveToFirst()) {
        while (!cursor.isAfterLast) {
          tables.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
          cursor.moveToNext()
        }
        databaseData.tables = tables
      }
    }
    return databaseData
  }

  fun getTableData(database: SQLiteDatabase, tableName: String): Table {
    val rows = ArrayList<Row>()
    val table = Table()
    val tableDataCursor = database.rawQuery("SELECT * FROM $tableName", null)

    tableDataCursor.use { cursor ->
      table.columns = listOf(*cursor!!.columnNames)
      if (cursor.moveToFirst()) {
        val columnCount = cursor.columnCount
        while (!cursor.isAfterLast) {
          val data = ArrayList<String>()
          for (i in 0 until columnCount) {
            data.add(cursor.getString(i))
          }
          val row = Row(data)
          rows.add(row)
          cursor.moveToNext()
        }
      }
      table.name = tableName
      table.rows = rows
    }
    return table
  }
}