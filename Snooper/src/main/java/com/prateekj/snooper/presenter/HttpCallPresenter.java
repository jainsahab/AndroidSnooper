package com.prateekj.snooper.presenter;

import com.prateekj.snooper.activity.HttpCallTab;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.views.HttpCallView;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static com.prateekj.snooper.activity.HttpCallTab.RESPONSE;
import static com.prateekj.snooper.model.HttpHeader.CONTENT_TYPE;

public class HttpCallPresenter {

  private HttpCall httpCall;
  private HttpCallView view;
  private ResponseFormatterFactory formatterFactory;
  private boolean requestViewLoaded;
  private boolean responseViewLoaded;

  public HttpCallPresenter(int callId, SnooperRepo snooperRepo, HttpCallView view, ResponseFormatterFactory formatterFactory) {
    this.httpCall = snooperRepo.findById(callId);
    this.view = view;
    this.formatterFactory = formatterFactory;
  }

  public void copyHttpCallBody(int selectedItem) {
    String text = getTextToCopy(selectedItem);
    view.copyToClipboard(text);
  }

  private String getTextToCopy(int selectedItem) {
    HttpCallTab httpCallTab = HttpCallTab.byIndex(selectedItem);
    String dataToCopy;
    HttpHeader contentTypeHeader;
    if (httpCallTab == RESPONSE) {
      contentTypeHeader = httpCall.getResponseHeader(CONTENT_TYPE);
      dataToCopy = httpCall.getResponseBody();
    } else {
      contentTypeHeader = httpCall.getRequestHeader(CONTENT_TYPE);
      dataToCopy = httpCall.getPayload();
    }
    if (contentHeadersPresent(contentTypeHeader)) {
      ResponseFormatter formatter = this.formatterFactory.getFor(contentTypeHeader.getValues().get(0).getValue());
      return formatter.format(dataToCopy);
    }
    return "";

  }

  private boolean contentHeadersPresent(HttpHeader contentTypeHeader) {
    return contentTypeHeader != null && contentTypeHeader.getValues().size() > 0;
  }

  public void onHttpCallBodyFormatted(int mode) {
    if (mode == REQUEST_MODE)
      requestViewLoaded = true;
    if (mode == RESPONSE_MODE)
      responseViewLoaded = true;
    if (requestViewLoaded && responseViewLoaded) {
      view.dismissProgressDialog();
    }
  }
}
