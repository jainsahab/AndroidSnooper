package com.prateekj.snooper.presenter;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;

import org.junit.Test;

import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpBodyPresenterTest {

  @Test
  public void shouldInitializeViewModelWithProperMode() throws Exception {
    int httpCallId = 5;
    HttpCall httpCall = mock(HttpCall.class);
    SnooperRepo repo = mock(SnooperRepo.class);
    HttpBodyViewModel viewModel = mock(HttpBodyViewModel.class);
    when(repo.findById(httpCallId)).thenReturn(httpCall);

    HttpBodyPresenter presenter = new HttpBodyPresenter(repo, httpCallId);
    presenter.init(viewModel, RESPONSE_MODE);

    verify(repo).findById(httpCallId);
    verify(viewModel).init(eq(httpCall), any(JsonResponseFormatter.class), eq(RESPONSE_MODE));
  }
}
