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
  public void shouldReturnFormattedBodyUsingFormatter() throws Exception {
    String payload = "payload";
    String formattedPayload = "formatted payload";
    ResponseBodyViewModel responseBodyViewModel = new ResponseBodyViewModel();
    ResponseFormatter formatter = mock(ResponseFormatter.class);
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getPayload()).thenReturn(payload);
    when(formatter.format(payload)).thenReturn(formattedPayload);
    responseBodyViewModel.init(httpCall, formatter);

    String actualFormattedPayload = responseBodyViewModel.formattedResponse();

    assertThat(actualFormattedPayload, is(formattedPayload));
  }
}