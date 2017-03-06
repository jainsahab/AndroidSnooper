package com.prateekj.snooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.XmlFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.model.HttpHeader.CONTENT_TYPE;

public class HttpBodyPresenter {
  private SnooperRepo repo;
  private int httpCallId;
  private int mode;

  public HttpBodyPresenter(SnooperRepo repo, int httpCallId) {
    this.repo = repo;
    this.httpCallId = httpCallId;
  }

  public void init(HttpBodyViewModel viewModel, int mode) {
    this.mode = mode;
    HttpCall httpCall = this.repo.findById(httpCallId);
    viewModel.init(httpCall, getFormatter(httpCall), mode);
  }

  @NonNull
  private ResponseFormatter getFormatter(HttpCall httpCall) {
    if(isXmlType(getContentTypeHeader(httpCall))) {
      return new XmlFormatter();
    }
    return new JsonResponseFormatter();
  }

  private boolean isXmlType(HttpHeader header) {
    HttpHeaderValue headerValue = header.getValues().get(0);
    return headerValue.getValue().toLowerCase().contains("xml");
  }

  private HttpHeader getContentTypeHeader(HttpCall httpCall) {
    if (this.mode == REQUEST_MODE) {
      return httpCall.getRequestHeader(CONTENT_TYPE);
    }
    return httpCall.getResponseHeader(CONTENT_TYPE);
  }
}
