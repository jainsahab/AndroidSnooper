package com.prateekj.snooper.networksnooper.presenter;

import com.prateekj.snooper.networksnooper.adapter.HttpCallListClickListener;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpListView;

import java.util.List;

import static com.prateekj.snooper.utils.CollectionUtilities.last;

public class HttpCallListPresenter implements HttpCallListClickListener {
  public static final int PAGE_SIZE = 20;
  private final SnooperRepo snooperRepo;
  private HttpListView httpListView;
  private long lastCallId;

  public HttpCallListPresenter(HttpListView httpListView, SnooperRepo snooperRepo) {
    this.httpListView = httpListView;
    this.snooperRepo = snooperRepo;
  }

  public void init() {
    List<HttpCallRecord> httpCallRecords = snooperRepo.findAllSortByDateAfter(-1, PAGE_SIZE);
    lastCallId = last(httpCallRecords).getId();
    httpListView.initHttpCallRecordList(httpCallRecords);
  }

  public void onNextPageCall() {
    List<HttpCallRecord> httpCallRecords = snooperRepo.findAllSortByDateAfter(lastCallId, PAGE_SIZE);
    lastCallId = last(httpCallRecords).getId();
    httpListView.appendRecordList(httpCallRecords);
  }

  @Override
  public void onClick(HttpCallRecord httpCall) {
    httpListView.navigateToResponseBody(httpCall.getId());
  }

  public void onDoneClick() {
    httpListView.finishView();
  }

  public void onDeleteRecordsClicked() {
    httpListView.showDeleteConfirmationDialog();
  }

  public void confirmDeleteRecords() {
    snooperRepo.deleteAll();
    httpListView.updateListViewAfterDelete();
  }
}
