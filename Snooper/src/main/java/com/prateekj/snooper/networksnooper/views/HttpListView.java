package com.prateekj.snooper.networksnooper.views;

public interface HttpListView {
  void navigateToResponseBody(long httpCallId);

  void finishView();

  void showDeleteConfirmationDialog();

  void updateListView();
}
