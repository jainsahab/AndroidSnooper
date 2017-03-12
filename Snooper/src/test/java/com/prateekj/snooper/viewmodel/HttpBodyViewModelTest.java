package com.prateekj.snooper.viewmodel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpBodyViewModelTest {

  @Test
  public void shouldReturnFormattedResponseBodyUsingFormatter() throws Exception {
    String formattedResponseBody = "formatted payload";
    HttpBodyViewModel httpBodyViewModel = new HttpBodyViewModel();
    httpBodyViewModel.init(formattedResponseBody);

    String actualFormattedPayload = httpBodyViewModel.getFormattedBody();

    assertThat(actualFormattedPayload, is(formattedResponseBody));
  }
}
