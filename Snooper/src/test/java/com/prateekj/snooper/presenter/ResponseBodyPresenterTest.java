package com.prateekj.snooper.presenter;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.ResponseBodyViewModel;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResponseBodyPresenterTest {

  @Test
  public void shouldPopulateResponseViewModel() throws Exception {
    int httpCallId = 5;
    HttpCall httpCall = mock(HttpCall.class);
    SnooperRepo repo = mock(SnooperRepo.class);
    ResponseBodyViewModel viewModel = mock(ResponseBodyViewModel.class);
    when(repo.findById(httpCallId)).thenReturn(httpCall);

    ResponseBodyPresenter presenter = new ResponseBodyPresenter(repo, httpCallId);
    presenter.init(viewModel);

    verify(repo).findById(httpCallId);
    verify(viewModel).init(eq(httpCall), any(JsonResponseFormatter.class));
  }
}