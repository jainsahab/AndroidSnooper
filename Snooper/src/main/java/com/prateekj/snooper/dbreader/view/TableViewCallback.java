package com.prateekj.snooper.dbreader.view;


import com.prateekj.snooper.dbreader.model.Table;

public interface TableViewCallback {
  void onTableFetchStarted();

  void onTableFetchCompleted(Table table);
}
