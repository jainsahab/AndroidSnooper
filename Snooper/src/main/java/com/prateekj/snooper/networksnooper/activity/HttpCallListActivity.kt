package com.prateekj.snooper.networksnooper.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.prateekj.snooper.R
import com.prateekj.snooper.customviews.DividerItemDecoration
import com.prateekj.snooper.customviews.NextPageRequestListener
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import com.prateekj.snooper.networksnooper.adapter.HttpCallListAdapter
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.presenter.HttpCallListPresenter
import com.prateekj.snooper.networksnooper.presenter.HttpCallListPresenter.Companion.PAGE_SIZE
import com.prateekj.snooper.networksnooper.views.HttpListView
import kotlinx.android.synthetic.main.activity_http_call_list.*

class HttpCallListActivity : SnooperBaseActivity(), HttpListView, NextPageRequestListener {

  private lateinit var presenter: HttpCallListPresenter
  private lateinit var httpCallListAdapter: HttpCallListAdapter
  private lateinit var repo: SnooperRepo
  private var areAllPagesLoaded: Boolean = false
  private var noCallsFound: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_http_call_list)
    setSupportActionBar(toolbar)
    repo = SnooperRepo(this)
    presenter = HttpCallListPresenter(this, repo)
    list.layoutManager = LinearLayoutManager(this)
    val itemDecoration =
      DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider)
    list.addItemDecoration(itemDecoration)
    list.itemAnimator = DefaultItemAnimator()
    list.setNextPageListener(this)
    presenter.init()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.http_call_list_menu, menu)
    if (noCallsFound) {
      menu.findItem(R.id.delete_records_menu).isVisible = false
    }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when {
      item.itemId == R.id.done_menu -> {
        presenter.onDoneClick()
        return true
      }
      item.itemId == R.id.delete_records_menu -> presenter.onDeleteRecordsClicked()
      item.itemId == R.id.search -> openSearchActivity()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun openSearchActivity() {
    startActivity(Intent(this, HttpCallSearchActivity::class.java))
  }


  override fun navigateToResponseBody(httpCallId: Long) {
    val intent = Intent(this, HttpCallActivity::class.java)
    intent.putExtra(HTTP_CALL_ID, httpCallId)
    startActivity(intent)
    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
  }

  override fun finishView() {
    finish()
  }

  override fun showDeleteConfirmationDialog() {
    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
      when (which) {
        DialogInterface.BUTTON_POSITIVE -> presenter.confirmDeleteRecords()

        DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
      }
    }
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.delete_records_dialog_text).setPositiveButton(
      getString(R.string.delete_records_dialog_confirmation),
      dialogClickListener
    )
      .setNegativeButton(
        getString(R.string.delete_records_dialog_cancellation),
        dialogClickListener
      ).show()
  }

  override fun updateListViewAfterDelete() {
    httpCallListAdapter.refreshData(repo.findAllSortByDateAfter(-1, 20))
    httpCallListAdapter.notifyDataSetChanged()
  }

  override fun initHttpCallRecordList(httpCallRecords: List<HttpCallRecord>) {
    httpCallListAdapter = HttpCallListAdapter(httpCallRecords.toMutableList(), presenter)
    checkIfAllPagesAreLoaded(httpCallRecords)
    list.adapter = httpCallListAdapter
  }

  override fun appendRecordList(httpCallRecords: List<HttpCallRecord>) {
    httpCallListAdapter.appendData(httpCallRecords)
    checkIfAllPagesAreLoaded(httpCallRecords)
    list.post { httpCallListAdapter.notifyDataSetChanged() }
  }

  override fun renderNoCallsFoundView() {
    noCallsFound = true
    http_call_list_container.visibility = GONE
    no_calls_found_container.visibility = VISIBLE
  }

  override fun requestNextPage() {
    presenter.onNextPageCall()
  }

  override fun areAllPagesLoaded(): Boolean {
    return areAllPagesLoaded
  }

  private fun checkIfAllPagesAreLoaded(httpCallRecords: List<HttpCallRecord>) {
    if (httpCallRecords.size < PAGE_SIZE) {
      areAllPagesLoaded = true
    }
  }
}
