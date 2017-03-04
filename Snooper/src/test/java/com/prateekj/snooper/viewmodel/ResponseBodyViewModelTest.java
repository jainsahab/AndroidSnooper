package com.prateekj.snooper.viewmodel;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.model.HttpCall;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseBodyViewModelTest {

  @Test
  public void shouldReturnFormattedResponseBodyUsingFormatter() throws Exception {
    String responseBody = "payload";
    String formattedResponseBody = "formatted payload";
    ResponseBodyViewModel responseBodyViewModel = new ResponseBodyViewModel();
    ResponseFormatter formatter = mock(ResponseFormatter.class);
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatter.format(responseBody)).thenReturn(formattedResponseBody);
    responseBodyViewModel.init(httpCall, formatter);

    String actualFormattedPayload = responseBodyViewModel.formattedResponse();

    assertThat(actualFormattedPayload, is(formattedResponseBody));
  }
}
