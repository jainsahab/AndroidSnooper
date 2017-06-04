package com.prateekj.snooper.networksnooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.HttpCallTab;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.views.HttpCallView;
import com.prateekj.snooper.utils.FileUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static com.prateekj.snooper.networksnooper.model.HttpHeader.CONTENT_TYPE;
import static java.lang.String.format;
import static java.util.Locale.US;

public class HttpCallPresenter {

  private final FileUtil fileUtil;
  private final BackgroundTaskExecutor executor;
  private HttpCall httpCall;
  private HttpCallView view;
  private ResponseFormatterFactory formatterFactory;

  public HttpCallPresenter(int callId, SnooperRepo snooperRepo, HttpCallView view, ResponseFormatterFactory formatterFactory, FileUtil fileUtil, BackgroundTaskExecutor executor) {
    this.httpCall = snooperRepo.findById(callId);
    this.view = view;
    this.formatterFactory = formatterFactory;
    this.fileUtil = fileUtil;
    this.executor = executor;
  }

  public void copyHttpCallBody(HttpCallTab httpCallTab) {
    String text = getTextToCopy(httpCallTab);
    view.copyToClipboard(text);
  }

  public void shareHttpCallBody() {
    final StringBuilder completeHttpCallData = getHttpCallData();
    final String logFileName = getLogFileName();
    executor.execute(new BackgroundTask<String>() {
      @Override
      public String onExecute() {
        return fileUtil.createLogFile(completeHttpCallData, logFileName);
      }

      @Override
      public void onResult(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
          view.shareData(filePath);
        }
      }
    });
  }

  public void onPermissionDenied() {
    view.showMessageShareNotAvailable();
  }

  @NonNull
  private String getLogFileName() {
    DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", US);
    return format("%s.txt", df.format(httpCall.getDate()));
  }

  private String getTextToCopy(HttpCallTab httpCallTab) {
    if (httpCallTab == RESPONSE) {
      return getFormattedData(httpCall.getResponseHeader(CONTENT_TYPE), httpCall.getResponseBody());
    } else if (httpCallTab == REQUEST) {
      return getFormattedData(httpCall.getRequestHeader(CONTENT_TYPE), httpCall.getPayload());
    }
    return httpCall.getError();
  }

  private boolean contentHeadersPresent(HttpHeader contentTypeHeader) {
    return contentTypeHeader != null && contentTypeHeader.getValues().size() > 0;
  }

  private StringBuilder getHttpCallData() {

    StringBuilder dataToCopy = new StringBuilder();
    HttpHeader contentTypeHeader;

    contentTypeHeader = httpCall.getRequestHeader(CONTENT_TYPE);
    String formattedRequestData = getFormattedData(contentTypeHeader, httpCall.getPayload());
    if (!StringUtils.isEmpty(formattedRequestData)) {
      dataToCopy.append("Request Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedRequestData);
      dataToCopy.append("\n");
    }

    contentTypeHeader = httpCall.getResponseHeader(CONTENT_TYPE);
    String formattedResponseData = getFormattedData(contentTypeHeader, httpCall.getResponseBody());

    if (!StringUtils.isEmpty(formattedResponseData)) {
      dataToCopy.append("Response Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedResponseData);
    }
    return dataToCopy;
  }

  private String getFormattedData(HttpHeader contentTypeHeader, String dataToCopy) {
    if (dataToCopy == null) {
      return "";
    }
    if (contentHeadersPresent(contentTypeHeader)) {
      ResponseFormatter formatter = this.formatterFactory.getFor(contentTypeHeader.getValues().get(0).getValue());
      return formatter.format(dataToCopy);
    }
    return dataToCopy;
  }
}
