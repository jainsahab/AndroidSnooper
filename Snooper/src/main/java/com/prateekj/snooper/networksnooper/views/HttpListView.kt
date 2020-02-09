package com.prateekj.snooper.networksnooper.views

import com.prateekj.snooper.networksnooper.model.HttpCallRecord

interface HttpListView {
  fun navigateToResponseBody(httpCallId: Long)
  fun finishView()
  fun showDeleteConfirmationDialog()
  fun updateListViewAfterDelete()
  fun initHttpCallRecordList(httpCallRecords: List<HttpCallRecord>)
  fun appendRecordList(httpCallRecords: List<HttpCallRecord>)
  fun renderNoCallsFoundView()
}
