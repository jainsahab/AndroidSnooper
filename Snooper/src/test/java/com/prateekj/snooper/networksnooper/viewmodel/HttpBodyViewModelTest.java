package com.prateekj.snooper.networksnooper.viewmodel;

import com.prateekj.snooper.BR;
import com.prateekj.snooper.ObservableViewModelTest;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpBodyViewModelTest extends ObservableViewModelTest {

  @Test
  public void shouldReturnFormattedResponseBodyUsingFormatter() throws Exception {
    String formattedResponseBody = "formatted payload";
    HttpBodyViewModel httpBodyViewModel = new HttpBodyViewModel();
    setObservable(httpBodyViewModel);
    httpBodyViewModel.init(formattedResponseBody);

    String actualFormattedPayload = httpBodyViewModel.getFormattedBody();

    assertTrue(isPropertyNotified(BR.formattedBody));
    assertThat(actualFormattedPayload, is(formattedResponseBody));
  }
}
