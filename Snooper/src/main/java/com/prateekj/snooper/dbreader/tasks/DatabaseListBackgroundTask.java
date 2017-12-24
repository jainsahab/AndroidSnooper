package com.prateekj.snooper.dbreader.tasks;

import android.content.Context;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseListBackgroundTask implements BackgroundTask<List<Database>> {

  private Context context;
  private DbReaderCallback dbReaderCallback;

  public DatabaseListBackgroundTask(Context context, DbReaderCallback dbReaderCallback) {
    this.context = context;
    this.dbReaderCallback = dbReaderCallback;
  }

  @Override
  public List<Database> onExecute() {
    String[] applicationDatabases = context.getApplicationContext().databaseList();
    if (!hasDatabases(applicationDatabases)) {
      return new ArrayList<>();
    }
    List<Database> dbFiles = new ArrayList<>();
    for (String dbFile : applicationDatabases) {
      if (dbFile.endsWith(".db") && !"snooper.db".equals(dbFile)) {
        Database database = new Database();
        File databasePath = context.getDatabasePath(dbFile);
        database.setPath(databasePath.getAbsolutePath());
        database.setName(databasePath.getName());
        dbFiles.add(database);
      }
    }
    return dbFiles;
  }

  private boolean hasDatabases(String[] applicationDatabases) {
    return applicationDatabases != null && applicationDatabases.length > 0;
  }

  @Override
  public void onResult(List<Database> databases) {
    dbReaderCallback.onApplicationDbFetchCompleted(databases);
  }
}
