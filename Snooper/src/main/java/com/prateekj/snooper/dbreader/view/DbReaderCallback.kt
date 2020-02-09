package com.prateekj.snooper.dbreader.view


import com.prateekj.snooper.dbreader.model.Database

interface DbReaderCallback {
  fun onDbFetchStarted()
  fun onApplicationDbFetchCompleted(databases: List<Database>)
}
