package com.prateekj.snooper.presenter;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.views.HttpListView;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallListPresenterTest {

  @Test
  public void shouldNotifyViewToNavigateToResponseBody() throws Exception {
    HttpListView view = mock(HttpListView.class);
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getId()).thenReturn(2);

    HttpCallListPresenter httpCallListPresenter = new HttpCallListPresenter(view);
    httpCallListPresenter.onClick(httpCall);

    verify(view).navigateToResponseBody(2);
  }
}