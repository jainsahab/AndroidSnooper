package com.prateekj.snooper.presenter;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.views.HttpListView;

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

  @Test
  public void shouldNotifyViewToNavigateToResponseBody() throws Exception {
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getId()).thenReturn(2);

    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view);
    httpCallListPresenter.onClick(httpCall);

    verify(view).navigateToResponseBody(2);
  }

  @Test
  public void shouldNotifyViewToFinishIt() throws Exception {
    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view);
    httpCallListPresenter.onDoneClick();

    verify(view).finishView();
  }
}