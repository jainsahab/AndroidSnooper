package com.prateekj.snooper.networksnooper.views;

public interface HttpListView {
  void navigateToResponseBody(int httpCallId);

  void finishView();

  void showDeleteConfirmationDialog();

  void updateListView();
}
