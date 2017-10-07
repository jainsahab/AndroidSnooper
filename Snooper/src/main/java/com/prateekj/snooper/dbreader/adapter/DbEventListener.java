package com.prateekj.snooper.dbreader.adapter;

import com.prateekj.snooper.dbreader.model.Database;

public interface DbEventListener {
  void onDatabaseClick(Database db);
}
