package com.prateekj.snooper.networksnooper.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView

import com.prateekj.snooper.R
import com.prateekj.snooper.customviews.DividerItemDecoration
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.adapter.HttpCallListAdapter
import com.prateekj.snooper.networksnooper.adapter.HttpCallListClickListener
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.presenter.HttpClassSearchPresenter
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView

import java.util.ArrayList

import android.view.View.GONE
import android.view.View.VISIBLE
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import kotlinx.android.synthetic.main.activity_http_call_search.*

class HttpCallSearchActivity : SnooperBaseActivity(), HttpCallSearchView, HttpCallListClickListener,
  SearchView.OnQueryTextListener {

  private var httpCallListAdapter: HttpCallListAdapter? = null
  private var httpClassSearchPresenter: HttpClassSearchPresenter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_http_call_search)
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    searchView.setOnQueryTextListener(this)
    list!!.layoutManager = LinearLayoutManager(this)
    val itemDecoration =
      DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider)
    list!!.addItemDecoration(itemDecoration)
    list!!.itemAnimator = DefaultItemAnimator()
    httpCallListAdapter = HttpCallListAdapter(ArrayList(), this)
    list!!.adapter = httpCallListAdapter
    httpClassSearchPresenter =
      HttpClassSearchPresenter(SnooperRepo(this), this, BackgroundTaskExecutor(this))

  }

  override fun showResults(httpCallRecords: List<HttpCallRecord>) {
    httpCallListAdapter!!.refreshData(httpCallRecords.toMutableList())
    httpCallListAdapter!!.notifyDataSetChanged()
    list!!.visibility = VISIBLE
  }

  override fun showNoResultsFoundMessage(keyword: String) {
    no_results_found.text = getString(R.string.no_results_found, keyword)
    no_results_found_container.visibility = VISIBLE
  }

  override fun hideSearchResultsView() {
    list!!.visibility = GONE
    no_results_found_container.visibility = GONE
  }

  override fun showLoader() {
    embedded_loader!!.visibility = VISIBLE
  }

  override fun hideLoader() {
    embedded_loader!!.visibility = GONE
  }

  override fun onClick(httpCall: HttpCallRecord) {
    val intent = Intent(this, HttpCallActivity::class.java)
    intent.putExtra(HTTP_CALL_ID, httpCall.id)
    startActivity(intent)
    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
    finish()
  }

  override fun onQueryTextSubmit(query: String): Boolean {
    return false
  }

  override fun onQueryTextChange(text: String): Boolean {
    httpClassSearchPresenter!!.searchCalls(text)
    return true
  }
}
