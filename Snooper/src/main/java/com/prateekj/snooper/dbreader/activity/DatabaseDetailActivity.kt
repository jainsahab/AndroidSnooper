package com.prateekj.snooper.dbreader.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.prateekj.snooper.R
import com.prateekj.snooper.dbreader.DatabaseDataReader
import com.prateekj.snooper.dbreader.DatabaseReader
import com.prateekj.snooper.dbreader.activity.DatabaseListActivity.Companion.DB_NAME
import com.prateekj.snooper.dbreader.adapter.TableAdapter
import com.prateekj.snooper.dbreader.adapter.TableEventListener
import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.view.DbViewCallback
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity
import kotlinx.android.synthetic.main.activity_db_view.*

class DatabaseDetailActivity : SnooperBaseActivity(), DbViewCallback, TableEventListener {
  private lateinit var databaseReader: DatabaseReader
  private lateinit var dbPath: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_db_view)
    initViews()
    dbPath = intent.getStringExtra(DatabaseListActivity.DB_PATH)
    val dbName = intent.getStringExtra(DB_NAME)
    val backgroundTaskExecutor = BackgroundTaskExecutor(this)
    databaseReader = DatabaseReader(this, backgroundTaskExecutor, DatabaseDataReader())
    databaseReader.fetchDbContent(this, dbPath, dbName)
  }

  override fun onDbFetchStarted() {
    embedded_loader!!.visibility = VISIBLE
  }

  override fun onDbFetchCompleted(databases: Database) {
    embedded_loader!!.visibility = GONE
    updateDbView(databases)
  }

  private fun updateDbView(database: Database) {
    db_name.text = database.name
    db_version.text = database.version.toString()
    updateTableList(database.tables!!)
  }

  private fun updateTableList(tables: List<String>) {
    val tableAdapter = TableAdapter(tables, this)
    val mLayoutManager = LinearLayoutManager(this)
    table_list.layoutManager = mLayoutManager
    table_list.itemAnimator = DefaultItemAnimator()
    table_list.adapter = tableAdapter
  }

  private fun initViews() {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }

  override fun onTableClick(table: String) {
    val dbViewActivity = Intent(this, TableDetailActivity::class.java)
    dbViewActivity.putExtra(TABLE_NAME, table)
    dbViewActivity.putExtra(DB_PATH, dbPath)
    startActivity(dbViewActivity)
  }

  companion object {
    const val TABLE_NAME = "TABLE_NAME"
    const val DB_PATH = "DB_PATH"
  }
}
