package com.prateekj.snooper.dbreader.view;


import com.prateekj.snooper.dbreader.model.Database;

public interface DbViewCallback {
  void onDbFetchStarted();

  void onDbFetchCompleted(Database databases);
}
