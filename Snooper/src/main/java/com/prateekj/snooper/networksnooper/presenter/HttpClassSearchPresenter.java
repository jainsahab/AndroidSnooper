package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HttpClassSearchPresenter {
  private SnooperRepo snooperRepo;
  private HttpCallSearchView httpCallSearchView;
  private BackgroundTaskExecutor taskExecutor;

  public HttpClassSearchPresenter(SnooperRepo snooperRepo, HttpCallSearchView httpCallSearchView, BackgroundTaskExecutor taskExecutor) {
    this.snooperRepo = snooperRepo;
    this.httpCallSearchView = httpCallSearchView;
    this.taskExecutor = taskExecutor;
  }

  public void searchCalls(final String text) {
    if (StringUtils.isEmpty(text)) {
      showResults(new ArrayList<HttpCallRecord>());
      return;
    }
    httpCallSearchView.hideSearchResultsView();
    httpCallSearchView.showLoader();
    taskExecutor.execute(searchHttpCallTask(text));
  }

  @NonNull
  private BackgroundTask<List<HttpCallRecord>> searchHttpCallTask(final String text) {
    return new BackgroundTask<List<HttpCallRecord>>() {
      @Override
      public List<HttpCallRecord> onExecute() {
        return snooperRepo.searchHttpRecord(text);
      }

      @Override
      public void onResult(List<HttpCallRecord> result) {
        if (result.isEmpty()){
          showNoResultsFoundMessage(text);
          return;
        }
        showResults(result);
      }
    };
  }

  private void showNoResultsFoundMessage(String text) {
    httpCallSearchView.hideLoader();
    httpCallSearchView.showNoResultsFoundMessage(text);
  }

  private void showResults(List<HttpCallRecord> result) {
    httpCallSearchView.hideLoader();
    httpCallSearchView.showResults(result);
  }
}
