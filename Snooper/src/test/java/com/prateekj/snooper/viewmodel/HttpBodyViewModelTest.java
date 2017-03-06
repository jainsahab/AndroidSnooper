package com.prateekj.snooper.viewmodel;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.model.HttpCall;

import org.junit.Test;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpBodyViewModelTest {

  @Test
  public void shouldReturnFormattedResponseBodyUsingFormatter() throws Exception {
    String responseBody = "payload";
    String formattedResponseBody = "formatted payload";
    HttpBodyViewModel httpBodyViewModel = new HttpBodyViewModel();
    ResponseFormatter formatter = mock(ResponseFormatter.class);
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatter.format(responseBody)).thenReturn(formattedResponseBody);
    httpBodyViewModel.init(httpCall, formatter, RESPONSE_MODE);

    String actualFormattedPayload = httpBodyViewModel.formattedBody();

    assertThat(actualFormattedPayload, is(formattedResponseBody));
  }

  @Test
  public void shouldReturnFormattedRequestBodyUsingFormatter() throws Exception {
    String requestBody = "payload";
    String formattedRequestBody = "formatted payload";
    HttpBodyViewModel httpBodyViewModel = new HttpBodyViewModel();
    ResponseFormatter formatter = mock(ResponseFormatter.class);
    HttpCall httpCall = mock(HttpCall.class);
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatter.format(requestBody)).thenReturn(formattedRequestBody);
    httpBodyViewModel.init(httpCall, formatter, REQUEST_MODE);

    String actualFormattedPayload = httpBodyViewModel.formattedBody();

    assertThat(actualFormattedPayload, is(formattedRequestBody));
  }
}
