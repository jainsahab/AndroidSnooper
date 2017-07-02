package com.prateekj.snooper.networksnooper.presenter;

import com.prateekj.snooper.networksnooper.adapter.HttpCallListClickListener;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpListView;

public class HttpCallListPresenter implements HttpCallListClickListener {
  private final SnooperRepo snooperRepo;
  private HttpListView httpListView;

  public HttpCallListPresenter(HttpListView httpListView, SnooperRepo snooperRepo) {
    this.httpListView = httpListView;
    this.snooperRepo = snooperRepo;
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
    httpListView.updateListView();
  }
}
