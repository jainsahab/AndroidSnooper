package com.prateekj.snooper.presenter;

import com.prateekj.snooper.adapter.HttpCallListClickListener;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.views.HttpListView;

public class HttpCallListPresenter implements HttpCallListClickListener {
  private HttpListView httpListView;

  public HttpCallListPresenter(HttpListView httpListView) {
    this.httpListView = httpListView;
  }

  @Override
  public void onClick(HttpCall httpCall) {
    httpListView.navigateToResponseBody(httpCall.getId());
  }

  public void onDoneClick() {
    httpListView.finishView();
  }
}
