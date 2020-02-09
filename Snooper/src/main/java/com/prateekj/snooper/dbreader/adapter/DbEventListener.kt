package com.prateekj.snooper.dbreader.adapter

import com.prateekj.snooper.dbreader.model.Database

interface DbEventListener {
  fun onDatabaseClick(db: Database)
}
