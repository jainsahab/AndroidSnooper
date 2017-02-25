package com.prateekj.snooper.presenter;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.ResponseBodyViewModel;

public class ResponseBodyPresenter {
  private SnooperRepo repo;
  private int httpCallId;

  public ResponseBodyPresenter(SnooperRepo repo, int httpCallId) {
    this.repo = repo;
    this.httpCallId = httpCallId;
  }

  public void init(ResponseBodyViewModel viewModel) {
    HttpCall httpCall = this.repo.findById(httpCallId);
    viewModel.init(httpCall, new JsonResponseFormatter());
  }
}
