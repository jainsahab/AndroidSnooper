package com.prateekj.snooper.dbreader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.model.Row;
import com.prateekj.snooper.dbreader.model.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseDataReader {

  public Database getData(SQLiteDatabase database) {
    Database databaseData = new Database();
    databaseData.setPath(database.getPath());
    databaseData.setVersion(database.getVersion());
    Cursor tableNameCursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
    List<String> tables = new ArrayList<>();
    if (tableNameCursor != null && tableNameCursor.moveToFirst()) {
      while (!tableNameCursor.isAfterLast()) {
        tables.add(tableNameCursor.getString(tableNameCursor.getColumnIndex("name")));
        tableNameCursor.moveToNext();
      }
      databaseData.setTables(tables);
    }
    tableNameCursor.close();
    return databaseData;
  }

  @NonNull
  public Table getTableData(SQLiteDatabase database, String tableName) {
    Table table = new Table();
    Cursor tableDataCursor = database.rawQuery("SELECT * FROM " + tableName, null);
    table.setColumns(Arrays.asList(tableDataCursor.getColumnNames()));
    List<Row> rows = new ArrayList<>();
    if (tableDataCursor != null && tableDataCursor.moveToFirst()) {
      int columnCount = tableDataCursor.getColumnCount();

      while (!tableDataCursor.isAfterLast()) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
          data.add(tableDataCursor.getString(i));
        }

        Row row = new Row(data);
        rows.add(row);

        tableDataCursor.moveToNext();
      }
    }
    table.setName(tableName);
    table.setRows(rows);
    tableDataCursor.close();
    return table;
  }
}