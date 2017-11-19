package com.prateekj.snooper.networksnooper.views;

import com.prateekj.snooper.networksnooper.model.HttpCallRecord;

import java.util.List;

public interface HttpListView {
  void navigateToResponseBody(long httpCallId);

  void finishView();

  void showDeleteConfirmationDialog();

  void updateListViewAfterDelete();

  void initHttpCallRecordList(List<HttpCallRecord> httpCallRecords);

  void appendRecordList(List<HttpCallRecord> httpCallRecords);

  void renderNoCallsFoundView();
}
