package com.prateekj.snooper.networksnooper.views;

import com.prateekj.snooper.networksnooper.model.HttpCallRecord;

import java.util.List;

public interface HttpCallSearchView {
  void showResults(List<HttpCallRecord> httpCallRecords);
  void showNoResultsFoundMessage(String keyword);
  void hideSearchResultsView();
  void showLoader();
  void hideLoader();
}
