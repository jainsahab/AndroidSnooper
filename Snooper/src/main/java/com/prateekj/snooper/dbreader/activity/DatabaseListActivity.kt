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
import com.prateekj.snooper.dbreader.adapter.DatabaseAdapter
import com.prateekj.snooper.dbreader.adapter.DbEventListener
import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.view.DbReaderCallback
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity
import kotlinx.android.synthetic.main.activity_db_reader.*

class DatabaseListActivity : SnooperBaseActivity(), DbReaderCallback, DbEventListener {
  private lateinit var adapter: DatabaseAdapter
  private lateinit var databaseReader: DatabaseReader

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_db_reader)
    initViews()

    val backgroundTaskExecutor = BackgroundTaskExecutor(this)
    databaseReader = DatabaseReader(this, backgroundTaskExecutor, DatabaseDataReader())
    databaseReader.fetchApplicationDatabases(this)
  }

  override fun onDbFetchStarted() {
    embedded_loader!!.visibility = VISIBLE
  }

  override fun onApplicationDbFetchCompleted(databases: List<Database>) {
    embedded_loader!!.visibility = GONE
    adapter = DatabaseAdapter(databases, this)
    val mLayoutManager = LinearLayoutManager(this)
    db_list.layoutManager = mLayoutManager
    db_list.itemAnimator = DefaultItemAnimator()
    db_list.adapter = adapter
  }

  override fun onDatabaseClick(db: Database) {
    val dbViewActivity = Intent(this@DatabaseListActivity, DatabaseDetailActivity::class.java)
    dbViewActivity.putExtra(DB_PATH, db.path)
    dbViewActivity.putExtra(DB_NAME, db.name)
    startActivity(dbViewActivity)
  }

  private fun initViews() {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
  }

  companion object {
    const val DB_PATH = "DB_PATH"
    const val DB_NAME = "DB_NAME"
  }
}
