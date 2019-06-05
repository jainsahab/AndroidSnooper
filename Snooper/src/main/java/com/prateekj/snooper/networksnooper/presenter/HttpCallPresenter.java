package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.HttpCallTab;
import com.prateekj.snooper.networksnooper.helper.DataCopyHelper;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpCallView;
import com.prateekj.snooper.utils.FileUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static java.lang.String.format;
import static java.util.Locale.US;

public class HttpCallPresenter {

  private final FileUtil fileUtil;
  private final BackgroundTaskExecutor executor;
  private HttpCallRecord httpCallRecord;
  private HttpCallView view;
  private DataCopyHelper dataCopyHelper;

  public HttpCallPresenter(DataCopyHelper dataCopyHelper, HttpCallRecord httpCallRecord, HttpCallView view, FileUtil fileUtil, BackgroundTaskExecutor executor) {
    this.dataCopyHelper = dataCopyHelper;
    this.httpCallRecord = httpCallRecord;
    this.view = view;
    this.fileUtil = fileUtil;
    this.executor = executor;
  }

  public void copyHttpCallBody(HttpCallTab httpCallTab) {
    String text = getTextToCopy(httpCallTab);
    view.copyToClipboard(text);
  }

  public void shareHttpCallBody() {
    final StringBuilder completeHttpCallData = dataCopyHelper.getHttpCallData();
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
    return format("%s.txt", df.format(httpCallRecord.getDate()));
  }

  private String getTextToCopy(HttpCallTab httpCallTab) {
    if (httpCallTab == RESPONSE) {
      return dataCopyHelper.getResponseDataForCopy();
    } else if (httpCallTab == REQUEST) {
      return dataCopyHelper.getRequestDataForCopy();
    } else if (httpCallTab == HEADERS) {
      return dataCopyHelper.getHeadersForCopy();
    }
    return dataCopyHelper.getErrorsForCopy();
  }
}
