package com.prateekj.snooper.networksnooper.presenter;

import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpCallListPresenterTest {

  @Mock
  private HttpListView view;
  @Mock
  private SnooperRepo repo;

  @Test
  public void shouldNotifyViewToNavigateToResponseBody() throws Exception {
    HttpCallRecord httpCall = mock(HttpCallRecord.class);
    when(httpCall.getId()).thenReturn(2L);

    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view, repo);
    httpCallListPresenter.onClick(httpCall);

    verify(view).navigateToResponseBody(2);
  }

  @Test
  public void shouldNotifyViewToFinishIt() throws Exception {
    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view, repo);
    httpCallListPresenter.onDoneClick();

    verify(view).finishView();
  }

  @Test
  public void shouldDeleteTheRecordsAndUpdateUi() throws Exception {
    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view, repo);
    httpCallListPresenter.confirmDeleteRecords();

    verify(repo).deleteAll();
    verify(view).updateListView();
  }

  @Test
  public void shouldShowConfirmationDialogOnClickOfDeleteRecords() throws Exception {
    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view, repo);
    httpCallListPresenter.onDeleteRecordsClicked();

    verify(view).showDeleteConfirmationDialog();
  }
}