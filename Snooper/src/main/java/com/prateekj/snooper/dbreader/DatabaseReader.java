package com.prateekj.snooper.dbreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
  private static final String TAG = DatabaseReader.class.getName();
  private Context context;
  private BackgroundTaskExecutor executor;
  private DbReaderCallback dbReaderCallback;

  public DatabaseReader(Context context, BackgroundTaskExecutor executor, DbReaderCallback dbReaderCallback) {
    this.context = context;
    this.executor = executor;
    this.dbReaderCallback = dbReaderCallback;
  }

  public void fetchApplicationDatabases() {
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
        dbReaderCallback.onDbFetchCompleted(databases);
      }
    });
  }

  public SQLiteDatabase getDatabase(String name) {
    SQLiteDatabase sqLiteDatabase = null;
    try {
      sqLiteDatabase = SQLiteDatabase.openDatabase(name, null, SQLiteDatabase.OPEN_READONLY);
    } catch (SQLiteException exception) {
      Logger.e(TAG, "Exception while opening the database", exception);
    }
    return sqLiteDatabase;
  }
}
