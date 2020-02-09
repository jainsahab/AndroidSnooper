package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.networksnooper.adapter.HttpCallListClickListener
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.views.HttpListView

class HttpCallListPresenter(
  private val httpListView: HttpListView,
  private val snooperRepo: SnooperRepo
) : HttpCallListClickListener {
  private var lastCallId: Long = 0

  fun init() {
    val httpCallRecords = snooperRepo.findAllSortByDateAfter(-1, PAGE_SIZE)
    if (httpCallRecords.isEmpty()) {
      httpListView.renderNoCallsFoundView()
      return
    }
    lastCallId = httpCallRecords.last().id
    httpListView.initHttpCallRecordList(httpCallRecords)
  }

  fun onNextPageCall() {
    val httpCallRecords = snooperRepo.findAllSortByDateAfter(lastCallId, PAGE_SIZE)
    lastCallId = httpCallRecords.last().id
    httpListView.appendRecordList(httpCallRecords)
  }

  override fun onClick(httpCall: HttpCallRecord) {
    httpListView.navigateToResponseBody(httpCall.id)
  }

  fun onDoneClick() {
    httpListView.finishView()
  }

  fun onDeleteRecordsClicked() {
    httpListView.showDeleteConfirmationDialog()
  }

  fun confirmDeleteRecords() {
    snooperRepo.deleteAll()
    httpListView.updateListViewAfterDelete()
  }

  companion object {
    const val PAGE_SIZE = 20
  }
}
