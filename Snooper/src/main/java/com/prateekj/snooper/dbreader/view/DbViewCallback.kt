package com.prateekj.snooper.dbreader.view


import com.prateekj.snooper.dbreader.model.Database

interface DbViewCallback {
  fun onDbFetchStarted()
  fun onDbFetchCompleted(databases: Database)
}
