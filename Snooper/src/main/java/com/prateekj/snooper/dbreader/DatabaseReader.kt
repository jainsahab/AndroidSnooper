package com.prateekj.snooper.dbreader

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException

import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.model.Table
import com.prateekj.snooper.dbreader.tasks.DatabaseListBackgroundTask
import com.prateekj.snooper.dbreader.view.DbReaderCallback
import com.prateekj.snooper.dbreader.view.DbViewCallback
import com.prateekj.snooper.dbreader.view.TableViewCallback
import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.utils.Logger

class DatabaseReader(
  private val context: Context,
  private val executor: BackgroundTaskExecutor,
  private val databaseDataReader: DatabaseDataReader
) {

  fun fetchApplicationDatabases(dbReaderCallback: DbReaderCallback) {
    dbReaderCallback.onDbFetchStarted()
    executor.execute(DatabaseListBackgroundTask(context, dbReaderCallback))
  }

  fun fetchDbContent(dbViewCallback: DbViewCallback, dbPath: String, dbName: String) {
    dbViewCallback.onDbFetchStarted()
    executor.execute(object : BackgroundTask<Database?> {
      override fun onExecute(): Database? {
        val database = getDatabase(dbPath)
        if (database != null) {
          val dbWithData = databaseDataReader.getData(database)
          dbWithData.name = dbName
          return dbWithData
        }
        return null
      }

      override fun onResult(result: Database?) {
        if (result != null) {
          dbViewCallback.onDbFetchCompleted(result)
        }
      }
    })
  }

  fun fetchTableContent(tableViewCallback: TableViewCallback, dbPath: String, tableName: String) {
    tableViewCallback.onTableFetchStarted()
    executor.execute(object : BackgroundTask<Table?> {
      override fun onExecute(): Table? {
        val database = getDatabase(dbPath)
        return if (database != null) {
          databaseDataReader.getTableData(database, tableName)
        } else null
      }

      override fun onResult(result: Table?) {
        if (result != null) {
          tableViewCallback.onTableFetchCompleted(result)
        }
      }
    })
  }


  private fun getDatabase(path: String): SQLiteDatabase? {
    var sqLiteDatabase: SQLiteDatabase? = null
    try {
      sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    } catch (exception: SQLiteException) {
      Logger.e(TAG, "Exception while opening the database", exception)
    }

    return sqLiteDatabase
  }

  companion object {
    private val TAG = DatabaseReader::class.java.name
  }
}