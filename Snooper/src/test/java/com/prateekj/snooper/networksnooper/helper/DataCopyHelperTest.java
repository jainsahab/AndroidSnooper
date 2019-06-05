package com.prateekj.snooper.networksnooper.helper;

import android.content.res.Resources;
import androidx.annotation.NonNull;

import com.prateekj.snooper.R;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataCopyHelperTest {

  private ResponseFormatterFactory formatterFactory;
  private HttpCallRecord httpCall;
  private ResponseFormatter responseFormatter;
  private DataCopyHelper dataCopyHelper;
  private Resources resources;

  @Before
  public void setUp() throws Exception {
    formatterFactory = mock(ResponseFormatterFactory.class);
    httpCall = mock(HttpCallRecord.class);
    responseFormatter = mock(ResponseFormatter.class);
    resources = mock(Resources.class);
    dataCopyHelper = new DataCopyHelper(httpCall, formatterFactory, resources);
    mockStringResources();
  }

  @Test
  public void shouldCopyResponseWithoutFormattingIfContentHeadersNullInResponseData() throws Exception {
    String responseBody = "response body";
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(null);
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);

    String responseDataForCopy = dataCopyHelper.getResponseDataForCopy();

    assertThat(responseDataForCopy, is(responseBody));
    verify(responseFormatter, never()).format(responseBody);
  }

  @Test
  public void shouldCopyResponseWithoutFormattingIfContentHeaderValuesIsMissingInResponseData() throws Exception {
    String responseBody = "response body";
    HttpHeader httpHeader = new HttpHeader();
    httpHeader.setValues(new ArrayList<HttpHeaderValue>());
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);

    String responseDataForCopy = dataCopyHelper.getResponseDataForCopy();

    assertThat(responseDataForCopy, is(responseBody));
    verify(responseFormatter, never()).format(responseBody);
  }

  @Test
  public void shouldCopyResponseWithFormattingIfContentHeadersPresentInResponseData() throws Exception {
    String responseBody = "response body";
    String formattedResponseBody = "formatted response body";
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(responseBody)).thenReturn(formattedResponseBody);

    String responseDataForCopy = dataCopyHelper.getResponseDataForCopy();

    assertThat(responseDataForCopy, is(formattedResponseBody));
    verify(responseFormatter).format(responseBody);
  }

  @Test
  public void shouldCopyEmptyStringWhenResponseIsNotPresent() throws Exception {
    when(httpCall.getResponseBody()).thenReturn(null);

    String responseDataForCopy = dataCopyHelper.getResponseDataForCopy();

    assertThat(responseDataForCopy, is(""));
  }

  @Test
  public void shouldCopyRequestWithoutFormattingIfContentHeaderIsNullInRequestData() throws Exception {
    String requestBody = "response body";
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(null);
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);

    String requestDataForCopy = dataCopyHelper.getRequestDataForCopy();

    assertThat(requestDataForCopy, is(requestBody));
    verify(responseFormatter, never()).format(requestBody);
  }

  @Test
  public void shouldCopyRequestWithoutFormattingIfContentHeaderValuesMissingInRequestData() throws Exception {
    String requestBody = "response body";
    HttpHeader httpHeader = new HttpHeader();
    httpHeader.setValues(new ArrayList<HttpHeaderValue>());
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);

    String requestDataForCopy = dataCopyHelper.getRequestDataForCopy();

    assertThat(requestDataForCopy, is(requestBody));
    verify(responseFormatter, never()).format(requestBody);
  }

  @Test
  public void shouldCopyRequestResponseHeadersPresent() throws Exception {
    HttpHeader httpHeader = getAcceptLanguageHttpHeader();

    when(httpCall.getRequestHeaders()).thenReturn(asList(httpHeader, getJsonContentTypeHeader()));
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseHeaders()).thenReturn(asList(httpHeader, getHeader()));
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());

    String copiedHeaders = dataCopyHelper.getHeadersForCopy();

    assertThat(copiedHeaders, is("\nRequest Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
      "Content-Type: application/json\n\nResponse Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
      "Header: headerValue\n"));
  }

  @Test
  public void shouldCopyOnlyRequestHeadersPresentIfResponseHeadersMissing() throws Exception {
    HttpHeader httpHeader = getAcceptLanguageHttpHeader();

    when(httpCall.getRequestHeaders()).thenReturn(asList(httpHeader, getJsonContentTypeHeader()));
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());

    String copiedHeaders = dataCopyHelper.getHeadersForCopy();

    assertThat(copiedHeaders, is("\nRequest Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
      "Content-Type: application/json\n"));
  }

  @Test
  public void shouldCopyOnlyResponseHeadersPresentIfRequestHeadersMissing() throws Exception {
    HttpHeader httpHeader = getAcceptLanguageHttpHeader();

    when(httpCall.getResponseHeaders()).thenReturn(asList(httpHeader, getHeader()));
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());

    String copiedHeaders = dataCopyHelper.getHeadersForCopy();

    assertThat(copiedHeaders, is("\nResponse Headers\naccept-language: en-US,en;q=0.8,hi;q=0.6\n" +
      "Header: headerValue\n"));
  }

  @Test
  public void shouldCopyEmptyStringWhenRequestIsNotPresent() throws Exception {
    when(httpCall.getResponseBody()).thenReturn(null);

    String requestDataForCopy = dataCopyHelper.getRequestDataForCopy();

    assertThat(requestDataForCopy, is(""));
  }

  @Test
  public void shouldAskViewToCopyTheError() throws Exception {
    String error = "error";
    when(httpCall.getError()).thenReturn(error);

    String errorsForCopy = dataCopyHelper.getErrorsForCopy();

    assertThat(errorsForCopy, is(error));
  }

  @Test
  public void shouldShareRequestResponseData() throws Exception {
    String requestBody = "request body";
    String formatRequestBody = "format Request body";
    String responseBody = "response body";
    String formatResponseBody = "format Response body";

    HttpHeader httpHeader = getAcceptLanguageHttpHeader();

    when(httpCall.getRequestHeaders()).thenReturn(asList(httpHeader, getJsonContentTypeHeader()));
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(httpCall.getResponseHeaders()).thenReturn(asList(httpHeader, getHeader()));
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(httpCall.getDate()).thenReturn(getDate(2017, 4, 12, 1, 2, 3));
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(requestBody)).thenReturn(formatRequestBody);
    when(responseFormatter.format(responseBody)).thenReturn(formatResponseBody);

    StringBuilder httpCallData = dataCopyHelper.getHttpCallData();

    assertThat(httpCallData.toString(), is("Request Body\nformat Request body\nRequest Headers\naccept-language: en-US," +
      "en;q=0.8,hi;q=0.6\nContent-Type: application/json\nResponse Body\nformat Response body\nResponse Headers\n" +
      "accept-language: en-US,en;q=0.8,hi;q=0.6\nHeader: headerValue\n"));
    verify(responseFormatter, times(1)).format(requestBody);
    verify(responseFormatter, times(1)).format(responseBody);
  }

  @Test
  public void shouldShareResponseDataOnlyIfRequestDataEmpty() throws Exception {
    String responseBody = "response body";
    String formatResponseBody = "format Response body";

    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(httpCall.getRequestHeaders()).thenReturn(null);
    when(httpCall.getDate()).thenReturn(getDate(2017, 4, 12, 1, 2, 3));
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(responseBody)).thenReturn(formatResponseBody);

    StringBuilder httpCallData = dataCopyHelper.getHttpCallData();

    assertThat(httpCallData.toString(), is("Response Body\nformat Response body"));
    verify(responseFormatter, times(1)).format(responseBody);
  }

  @Test
  public void shouldShareRequestDataOnlyIfResponseDataEmpty() throws Exception {
    String requestBody = "request body";
    String formatRequestBody = "format Request body";

    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(httpCall.getResponseBody()).thenReturn(null);
    when(httpCall.getDate()).thenReturn(getDate(2017, 4, 12, 1, 2, 3));
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(requestBody)).thenReturn(formatRequestBody);

    StringBuilder httpCallData = dataCopyHelper.getHttpCallData();

    assertThat(httpCallData.toString(), is("Request Body\nformat Request body"));
    verify(responseFormatter, times(1)).format(requestBody);
  }

  @NonNull
  private HttpHeader getJsonContentTypeHeader() {
    HttpHeaderValue headerValue = new HttpHeaderValue("application/json");
    HttpHeader httpHeader = new HttpHeader("Content-Type");
    httpHeader.setValues(Collections.singletonList(headerValue));
    return httpHeader;
  }

  @NonNull
  private HttpHeader getHeader() {
    HttpHeaderValue headerValue = new HttpHeaderValue("headerValue");
    HttpHeader httpHeader = new HttpHeader("Header");
    httpHeader.setValues(Collections.singletonList(headerValue));
    return httpHeader;
  }

  @NonNull
  private HttpHeader getAcceptLanguageHttpHeader() {
    HttpHeader httpHeader = new HttpHeader("accept-language");
    HttpHeaderValue value1 = new HttpHeaderValue("en-US,en");
    HttpHeaderValue value2 = new HttpHeaderValue("q=0.8,hi");
    HttpHeaderValue value3 = new HttpHeaderValue("q=0.6");
    httpHeader.setValues(asList(value1, value2, value3));
    return httpHeader;
  }

  private void mockStringResources() {
    when(resources.getString(R.string.request_body_heading)).thenReturn("Request Body");
    when(resources.getString(R.string.request_headers)).thenReturn("Request Headers");
    when(resources.getString(R.string.response_body_heading)).thenReturn("Response Body");
    when(resources.getString(R.string.response_headers)).thenReturn("Response Headers");
  }
}