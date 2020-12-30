package com.prateekj.snooper.dbreader.tasks

import android.content.Context
import com.prateekj.snooper.database.SnooperDbHelper.Companion.DATABASE_NAME

import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.view.DbReaderCallback
import com.prateekj.snooper.infra.BackgroundTask

import java.util.ArrayList

class DatabaseListBackgroundTask(
  private val context: Context,
  private val dbReaderCallback: DbReaderCallback
) : BackgroundTask<List<Database>> {

  override fun onExecute(): List<Database> {
    val applicationDatabases = context.applicationContext.databaseList()
    if (!hasDatabases(applicationDatabases)) {
      return ArrayList()
    }

    return applicationDatabases
      .filter { it.endsWith(".db") && DATABASE_NAME != it }
      .map {
        Database().apply {
          val databasePath = context.getDatabasePath(it)
          path = databasePath.absolutePath
          name = databasePath.name
        }
      }
  }

  private fun hasDatabases(applicationDatabases: Array<String>?): Boolean {
    return applicationDatabases != null && applicationDatabases.isNotEmpty()
  }

  override fun onResult(result: List<Database>) {
    dbReaderCallback.onApplicationDbFetchCompleted(result)
  }
}
