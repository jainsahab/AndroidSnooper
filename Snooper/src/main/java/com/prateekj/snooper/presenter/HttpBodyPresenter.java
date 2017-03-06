package com.prateekj.snooper.presenter;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;

public class HttpBodyPresenter {
  private SnooperRepo repo;
  private int httpCallId;

  public HttpBodyPresenter(SnooperRepo repo, int httpCallId) {
    this.repo = repo;
    this.httpCallId = httpCallId;
  }

  public void init(HttpBodyViewModel viewModel, int mode) {
    HttpCall httpCall = this.repo.findById(httpCallId);
    viewModel.init(httpCall, new JsonResponseFormatter(), mode);
  }
}
