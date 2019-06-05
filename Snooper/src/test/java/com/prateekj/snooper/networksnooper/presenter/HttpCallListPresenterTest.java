package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpCallListPresenterTest {

  @Mock
  private HttpListView view;
  @Mock
  private SnooperRepo repo;
  private HttpCallListPresenter httpCallListPresenter;

  @Before
  public void setUp() throws Exception {
    this.httpCallListPresenter = new HttpCallListPresenter(view, repo);
  }

  @Test
  public void shouldInitializeHttpCallRecordsList() throws Exception {
    List<HttpCallRecord> httpCallRecords = singletonList(mock(HttpCallRecord.class));
    when(repo.findAllSortByDateAfter(-1, 20)).thenReturn(httpCallRecords);
    httpCallListPresenter.init();

    verify(view).initHttpCallRecordList(httpCallRecords);
  }

  @Test
  public void shouldShowNoCallsFoundMessage() throws Exception {
    when(repo.findAllSortByDateAfter(-1, 20)).thenReturn(new ArrayList<HttpCallRecord>());
    httpCallListPresenter.init();

    verify(view).renderNoCallsFoundView();
    verify(view, never()).initHttpCallRecordList(any(List.class));
  }

  @Test
  public void shouldQueryNextSetOfRecordOnNextPageCall() throws Exception {
    List<HttpCallRecord> lastSetHttpCalls = asList(createCallWithId(1));
    List<HttpCallRecord> nextSetHttpCalls = asList(createCallWithId(3), createCallWithId(2));
    when(repo.findAllSortByDateAfter(-1, 20)).thenReturn(asList(createCallWithId(5), createCallWithId(4)));
    httpCallListPresenter.init();

    when(repo.findAllSortByDateAfter(4, 20)).thenReturn(nextSetHttpCalls);
    httpCallListPresenter.onNextPageCall();
    verify(repo).findAllSortByDateAfter(4, 20);
    verify(view).appendRecordList(nextSetHttpCalls);

    when(repo.findAllSortByDateAfter(2, 20)).thenReturn(lastSetHttpCalls);
    httpCallListPresenter.onNextPageCall();
    verify(repo).findAllSortByDateAfter(2, 20);
    verify(view).appendRecordList(lastSetHttpCalls);
  }

  @Test
  public void shouldNotifyViewToNavigateToResponseBody() throws Exception {
    HttpCallRecord httpCall = mock(HttpCallRecord.class);
    when(httpCall.getId()).thenReturn(2L);

    httpCallListPresenter.onClick(httpCall);

    verify(view).navigateToResponseBody(2);
  }

  @Test
  public void shouldNotifyViewToFinishIt() throws Exception {
    httpCallListPresenter.onDoneClick();

    verify(view).finishView();
  }

  @Test
  public void shouldDeleteTheRecordsAndUpdateUi() throws Exception {
    httpCallListPresenter.confirmDeleteRecords();

    verify(repo).deleteAll();
    verify(view).updateListViewAfterDelete();
  }

  @Test
  public void shouldShowConfirmationDialogOnClickOfDeleteRecords() throws Exception {
    httpCallListPresenter.onDeleteRecordsClicked();

    verify(view).showDeleteConfirmationDialog();
  }

  @NonNull
  private HttpCallRecord createCallWithId(int id) {
    HttpCallRecord firstHttpCall = HttpCallRecord.from(new HttpCall.Builder().build());
    firstHttpCall.setId(id);
    return firstHttpCall;
  }
}