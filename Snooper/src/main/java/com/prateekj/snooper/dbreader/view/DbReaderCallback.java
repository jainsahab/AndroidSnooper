package com.prateekj.snooper.dbreader.view;


import com.prateekj.snooper.dbreader.model.Database;

import java.util.List;

public interface DbReaderCallback {
  void onDbFetchStarted();

  void onDbFetchCompleted(List<Database> databases);
}
