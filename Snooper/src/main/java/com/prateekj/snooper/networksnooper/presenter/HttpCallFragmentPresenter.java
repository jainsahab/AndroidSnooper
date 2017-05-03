package com.prateekj.snooper.networksnooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.PlainTextFormatter;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.networksnooper.model.HttpHeader.CONTENT_TYPE;

public class HttpCallFragmentPresenter {
  private SnooperRepo repo;
  private int httpCallId;
  private HttpCallBodyView httpCallBodyView;
  private ResponseFormatterFactory formatterFactory;
  private BackgroundTaskExecutor executor;
  private int mode;

  public HttpCallFragmentPresenter(SnooperRepo repo,
                                   int httpCallId,
                                   HttpCallBodyView httpCallBodyView,
                                   ResponseFormatterFactory formatterFactory,
                                   BackgroundTaskExecutor executor) {
    this.repo = repo;
    this.httpCallId = httpCallId;
    this.httpCallBodyView = httpCallBodyView;
    this.formatterFactory = formatterFactory;
    this.executor = executor;
  }

  public void init(final HttpBodyViewModel viewModel, int mode) {
    this.mode = mode;
    final HttpCall httpCall = this.repo.findById(httpCallId);
    final ResponseFormatter formatter = getFormatter(httpCall);
    final String bodyToFormat = getBodyToFormat(httpCall);
    executor.execute(new BackgroundTask<String>() {
      @Override
      public String onExecute() {
        return formatter.format(bodyToFormat);
      }

      @Override
      public void onResult(String result) {
        viewModel.init(result);
        httpCallBodyView.onFormattingDone();
      }
    });
  }

  private String getBodyToFormat(HttpCall httpCall) {
    return this.mode == REQUEST_MODE ? httpCall.getPayload() : httpCall.getResponseBody();
  }

  @NonNull
  private ResponseFormatter getFormatter(HttpCall httpCall) {
    HttpHeader contentTypeHeader = getContentTypeHeader(httpCall);
    if (contentTypeHeader == null) {
      return new PlainTextFormatter();
    }
    HttpHeaderValue headerValue = contentTypeHeader.getValues().get(0);
    return this.formatterFactory.getFor(headerValue.getValue());
  }

  private HttpHeader getContentTypeHeader(HttpCall httpCall) {
    if (this.mode == REQUEST_MODE) {
      return httpCall.getRequestHeader(CONTENT_TYPE);
    }
    return httpCall.getResponseHeader(CONTENT_TYPE);
  }
}
