package com.prateekj.snooper.dbreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.model.Table;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.dbreader.view.DbViewCallback;
import com.prateekj.snooper.dbreader.view.TableViewCallback;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
  private static final String TAG = DatabaseReader.class.getName();
  private final Context context;
  private final BackgroundTaskExecutor executor;
  private final DbDataReader dbDataReader;

  public DatabaseReader(Context context, BackgroundTaskExecutor executor, DbDataReader dbDataReader) {
    this.context = context;
    this.executor = executor;
    this.dbDataReader = dbDataReader;
  }

  public void fetchApplicationDatabases(final DbReaderCallback dbReaderCallback) {
    dbReaderCallback.onDbFetchStarted();
    executor.execute(new BackgroundTask<List<Database>>() {
      @Override
      public List<Database> onExecute() {
        String[] applicationDatabases = context.getApplicationContext().databaseList();
        List<Database> dbFiles = new ArrayList<>();
        if (applicationDatabases != null && applicationDatabases.length > 0) {
          for (String dbFile : applicationDatabases) {
            if (dbFile.endsWith(".db") && !dbFile.equals("snooper.db")) {
              Database database = new Database();
              File databasePath = context.getDatabasePath(dbFile);
              database.setPath(databasePath.getAbsolutePath());
              database.setName(databasePath.getName());
              dbFiles.add(database);
            }
          }
        }
        return dbFiles;
      }

      @Override
      public void onResult(List<Database> databases) {
        dbReaderCallback.onApplicationDbFetchCompleted(databases);
      }
    });
  }

  public void fetchDbContent(final DbViewCallback dbViewCallback, final String dbPath, final String dbName) {
    dbViewCallback.onDbFetchStarted();
    executor.execute(new BackgroundTask<Database>() {
      @Override
      public Database onExecute() {
        SQLiteDatabase database = getDatabase(dbPath);
        if (database != null) {
          Database dbWithData = dbDataReader.getData(database);
          dbWithData.setName(dbName);
          return dbWithData;
        }
        return null;
      }

      @Override
      public void onResult(Database dbWithData) {
        dbViewCallback.onDbFetchCompleted(dbWithData);
      }
    });
  }

  public void fetchTableContent(final TableViewCallback tableViewCallback, final String dbPath, final String tableName) {
    tableViewCallback.onTableFetchStarted();
    executor.execute(new BackgroundTask<Table>() {
      @Override
      public Table onExecute() {
        SQLiteDatabase database = getDatabase(dbPath);
        if (database != null) {
          Table table = dbDataReader.getTableData(database, tableName);
          return table;
        }
        return null;
      }

      @Override
      public void onResult(Table table) {
        tableViewCallback.onTableFetchCompleted(table);
      }
    });
  }


  private SQLiteDatabase getDatabase(String path) {
    SQLiteDatabase sqLiteDatabase = null;
    try {
      sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    } catch (SQLiteException exception) {
      Logger.e(TAG, "Exception while opening the database", exception);
    }
    return sqLiteDatabase;
  }
}
