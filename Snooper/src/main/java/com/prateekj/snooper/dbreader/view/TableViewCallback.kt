package com.prateekj.snooper.dbreader.view


import com.prateekj.snooper.dbreader.model.Table

interface TableViewCallback {
  fun onTableFetchStarted()
  fun onTableFetchCompleted(table: Table)
}
