package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.formatter.PlainTextFormatter;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.Bound;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.ERROR_MODE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.networksnooper.model.HttpHeader.CONTENT_TYPE;

public class HttpCallFragmentPresenter {
  private SnooperRepo repo;
  private long httpCallId;
  private HttpCallBodyView httpCallBodyView;
  private ResponseFormatterFactory formatterFactory;
  private BackgroundTaskExecutor executor;
  private int mode;
  private String formattedBodyLowerCase;

  public HttpCallFragmentPresenter(SnooperRepo repo,
                                   long httpCallId,
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
    final HttpCallRecord httpCallRecord = this.repo.findById(httpCallId);
    final ResponseFormatter formatter = getFormatter(httpCallRecord);
    final String bodyToFormat = getBodyToFormat(httpCallRecord);
    executor.execute(new BackgroundTask<String>() {
      @Override
      public String onExecute() {
        String formattedBody = formatter.format(bodyToFormat);
        formattedBodyLowerCase = formattedBody.toLowerCase();
        return formattedBody;
      }

      @Override
      public void onResult(String result) {
        viewModel.init(result);
        httpCallBodyView.onFormattingDone();
      }
    });
  }

  public void searchInBody(String pattern) {
    httpCallBodyView.removeOldHighlightedSpans();
    if (StringUtils.isEmpty(pattern)) {
      return;
    }
    ArrayList<Bound> bounds = new ArrayList<>();
    int indexOfKeyword = formattedBodyLowerCase.indexOf(pattern);
    while (indexOfKeyword > -1) {
      int rightBound = indexOfKeyword + pattern.length();
      bounds.add(new Bound(indexOfKeyword, rightBound));
      indexOfKeyword = formattedBodyLowerCase.indexOf(pattern, rightBound);
    }
    if (!bounds.isEmpty()) {
      httpCallBodyView.highlightBounds(bounds);
    }
  }

  private String getBodyToFormat(HttpCallRecord httpCallRecord) {
    if (this.mode == ERROR_MODE) {
      return httpCallRecord.getError();
    }
    return this.mode == REQUEST_MODE ? httpCallRecord.getPayload() : httpCallRecord.getResponseBody();
  }

  @NonNull
  private ResponseFormatter getFormatter(HttpCallRecord httpCallRecord) {
    HttpHeader contentTypeHeader = getContentTypeHeader(httpCallRecord);
    if (contentTypeHeader == null) {
      return new PlainTextFormatter();
    }
    HttpHeaderValue headerValue = contentTypeHeader.getValues().get(0);
    return this.formatterFactory.getFor(headerValue.getValue());
  }

  private HttpHeader getContentTypeHeader(HttpCallRecord httpCall) {
    if (this.mode == REQUEST_MODE) {
      return httpCall.getRequestHeader(CONTENT_TYPE);
    }
    return httpCall.getResponseHeader(CONTENT_TYPE);
  }
}
