package com.prateekj.snooper.dbreader.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import com.prateekj.snooper.R
import com.prateekj.snooper.dbreader.DatabaseDataReader
import com.prateekj.snooper.dbreader.DatabaseReader
import com.prateekj.snooper.dbreader.activity.DatabaseDetailActivity.Companion.TABLE_NAME
import com.prateekj.snooper.dbreader.model.Table
import com.prateekj.snooper.dbreader.view.TableViewCallback
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity
import kotlinx.android.synthetic.main.activity_table_view.*

class TableDetailActivity : SnooperBaseActivity(), TableViewCallback {
  private lateinit var databaseReader: DatabaseReader

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_table_view)
    initViews()
    val tableName = intent.getStringExtra(TABLE_NAME)
    val dbPath = intent.getStringExtra(DatabaseDetailActivity.DB_PATH)
    val backgroundTaskExecutor = BackgroundTaskExecutor(this)
    databaseReader = DatabaseReader(this, backgroundTaskExecutor, DatabaseDataReader())
    databaseReader.fetchTableContent(this, dbPath, tableName!!)
  }

  override fun onTableFetchStarted() {
    embedded_loader!!.visibility = VISIBLE
  }

  override fun onTableFetchCompleted(table: Table) {
    embedded_loader!!.visibility = GONE
    updateView(table)
  }

  private fun initViews() {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

  }

  private fun updateView(table: Table) {
    addTableColumnNames(table)
    addTableRowsToUi(table)
  }

  private fun addTableRowsToUi(table: Table) {
    val rows = table.rows!!
    for (i in rows.indices) {
      table_layout!!.addView(addRowData(rows[i].data, i + 1))
    }
  }

  private fun addTableColumnNames(table: Table) {
    val columnRow = TableRow(this)
    val serialNoCell = getCellView(getString(R.string.serial_number_column_heading))
    serialNoCell.setTypeface(null, Typeface.BOLD)
    columnRow.addView(serialNoCell)
    for (column in table.columns!!) {
      val columnView = getCellView(column).apply {
        setBackgroundColor(ContextCompat.getColor(this@TableDetailActivity, R.color.snooper_grey))
        background = getDrawable(this@TableDetailActivity, R.drawable.table_cell_background)
        setTypeface(null, Typeface.BOLD)
      }
      columnRow.addView(columnView)
    }
    table_layout!!.addView(columnRow)
  }

  private fun addRowData(data: List<String>, serialNumber: Int): TableRow {
    val row = TableRow(this)
    row.addView(getCellView(serialNumber.toString()))
    for (cellValue in data) {
      row.addView(getCellView(cellValue))
    }
    return row
  }

  private fun getCellView(cellValue: String): TextView {
    val textView = TextView(this)
    textView.setPadding(1, 0, 0, 0)
    textView.background = getDrawable(this, R.drawable.table_cell_background)
    textView.text = cellValue
    return textView
  }
}