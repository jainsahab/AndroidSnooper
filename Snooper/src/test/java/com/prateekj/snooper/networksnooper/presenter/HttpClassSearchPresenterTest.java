package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView;

import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class HttpClassSearchPresenterTest {

  private BackgroundTaskExecutor mockExecutor;
  private HttpCallSearchView httpCallSearchView;
  private SnooperRepo repo;
  private HttpClassSearchPresenter searchPresenter;

  @Before
  public void setUp() throws Exception {
    mockExecutor = mock(BackgroundTaskExecutor.class);
    httpCallSearchView = mock(HttpCallSearchView.class);
    repo = mock(SnooperRepo.class);
    searchPresenter = new HttpClassSearchPresenter(repo, httpCallSearchView, mockExecutor);
  }

  @Test
  public void shouldShowSearchedHttpCallRecordsOnView() throws Exception {
    List<HttpCallRecord> httpCallRecordList = Arrays.asList(mock(HttpCallRecord.class));
    resolveBackgroundTask();
    when(repo.searchHttpRecord("url")).thenReturn(httpCallRecordList);

    searchPresenter.searchCalls("url");

    verify(repo).searchHttpRecord("url");

    verify(httpCallSearchView).hideSearchResultsView();
    verify(httpCallSearchView).showLoader();
    verify(httpCallSearchView).hideLoader();
    verify(httpCallSearchView).showResults(argThat(sameInstance(httpCallRecordList)));
  }

  @Test
  public void shouldShowNoResultsFoundMessage() throws Exception {
    List<HttpCallRecord> httpCallRecordList = new ArrayList<>();
    resolveBackgroundTask();
    when(repo.searchHttpRecord("url")).thenReturn(httpCallRecordList);

    searchPresenter.searchCalls("url");

    verify(repo).searchHttpRecord("url");

    verify(httpCallSearchView).hideSearchResultsView();
    verify(httpCallSearchView).showLoader();
    verify(httpCallSearchView).hideLoader();
    verify(httpCallSearchView).showNoResultsFoundMessage("url");
  }

  @Test
  public void shouldShowEmptySearchedHttpCallRecordsWhenTextIsEmpty() throws Exception {
    searchPresenter.searchCalls("");

    verify(repo, times(0)).searchHttpRecord("url");

    verify(httpCallSearchView).hideLoader();
    verify(httpCallSearchView).showResults(argThat(withSize(0)));
    verifyNoMoreInteractions(httpCallSearchView);
  }

  @NonNull
  private CustomTypeSafeMatcher<List<HttpCallRecord>> withSize(final int size) {
    return new CustomTypeSafeMatcher<List<HttpCallRecord>>("with size") {
      @Override
      protected boolean matchesSafely(List<HttpCallRecord> item) {
        return item.size() == size;
      }
    };
  }

  private void resolveBackgroundTask() {
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        BackgroundTask<List<HttpCallRecord>> backgroundTask = (BackgroundTask<List<HttpCallRecord>>) invocation.getArguments()[0];
        backgroundTask.onResult(backgroundTask.onExecute());
        return null;
      }
    }).when(mockExecutor).execute(any(BackgroundTask.class));
  }

}