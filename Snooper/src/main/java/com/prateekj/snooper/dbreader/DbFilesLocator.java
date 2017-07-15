package com.prateekj.snooper.dbreader;

import android.content.Context;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbFilesLocator {
  private static final String TAG = DbFilesLocator.class.getName();
  private Context context;
  private BackgroundTaskExecutor executor;
  private DbReaderCallback dbReaderCallback;

  public DbFilesLocator(Context context, BackgroundTaskExecutor executor, DbReaderCallback dbReaderCallback) {
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
}
