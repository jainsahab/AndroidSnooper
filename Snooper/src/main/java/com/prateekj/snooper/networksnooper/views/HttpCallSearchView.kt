package com.prateekj.snooper.networksnooper.views

import com.prateekj.snooper.networksnooper.model.HttpCallRecord

interface HttpCallSearchView {
  fun showResults(httpCallRecords: List<HttpCallRecord>)
  fun showNoResultsFoundMessage(keyword: String)
  fun hideSearchResultsView()
  fun showLoader()
  fun hideLoader()
}
