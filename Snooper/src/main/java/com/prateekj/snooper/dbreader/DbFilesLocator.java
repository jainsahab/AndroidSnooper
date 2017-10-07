package com.prateekj.snooper.dbreader;

import android.content.Context;

import com.prateekj.snooper.dbreader.model.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbFilesLocator {
  private Context context;

  public DbFilesLocator(Context context) {
    this.context = context;
  }

  public List<Database> fetchApplicationDatabases() {
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
}
