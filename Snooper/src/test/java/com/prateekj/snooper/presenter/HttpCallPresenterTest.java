package com.prateekj.snooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.views.HttpCallView;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallPresenterTest {

  private ResponseFormatterFactory formatterFactory;
  private HttpCallView view;
  private HttpCall httpCall;
  private ResponseFormatter responseFormatter;
  private SnooperRepo repo;

  @Before
  public void setUp() throws Exception {
    formatterFactory = mock(ResponseFormatterFactory.class);
    view = mock(HttpCallView.class);
    httpCall = mock(HttpCall.class);
    repo = mock(SnooperRepo.class);
    responseFormatter = mock(ResponseFormatter.class);
    when(repo.findById(1)).thenReturn(httpCall);
  }

  @Test
  public void shouldAskViewToCopyTheResponseData() throws Exception {
    String responseBody = "response body";
    String formatResponseBody = "format Response body";
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(responseBody)).thenReturn(formatResponseBody);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory);

    httpCallPresenter.copyHttpCallBody(0);
    verify(responseFormatter).format(responseBody);
    verify(view).copyToClipboard(formatResponseBody);
  }

  @Test
  public void shouldNotAskViewToCopyTheResponseDataIfContentHeaderIsMissing() throws Exception {
    String responseBody = "response body";
    String expectedCopiedData = "";
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(null);
    when(httpCall.getResponseBody()).thenReturn(null);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory);

    httpCallPresenter.copyHttpCallBody(0);
    verify(responseFormatter, never()).format(responseBody);
    verify(view).copyToClipboard(expectedCopiedData);
  }

  @Test
  public void shouldAskViewToCopyTheRequestData() throws Exception {
    String requestBody = "response body";
    String formatRequestBody = "format Request body";
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(requestBody)).thenReturn(formatRequestBody);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory);

    httpCallPresenter.copyHttpCallBody(1);
    verify(responseFormatter).format(requestBody);
    verify(view).copyToClipboard(formatRequestBody);
  }

  @Test
  public void shouldNotAskViewToCopyTheRequestDataIfContentHeaderIsMissing() throws Exception {
    String requestBody = "response body";
    String formatRequestBody = "";
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(null);
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory);

    httpCallPresenter.copyHttpCallBody(1);
    verify(responseFormatter, never()).format(requestBody);
    verify(view).copyToClipboard(formatRequestBody);
  }

  @Test
  public void shouldDismissDialogWhenRequestAndResponseDataHasBeenLoaded() throws Exception {
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory);

    httpCallPresenter.onHttpCallBodyFormatted(REQUEST_MODE);
    verify(view, never()).dismissProgressDialog();

    httpCallPresenter.onHttpCallBodyFormatted(RESPONSE_MODE);
    verify(view).dismissProgressDialog();
  }

  @NonNull
  private HttpHeader getJsonContentTypeHeader() {
    HttpHeaderValue headerValue = new HttpHeaderValue("application/json");
    HttpHeader httpHeader = new HttpHeader("Content-Type");
    httpHeader.setValues(Collections.singletonList(headerValue));
    return httpHeader;
  }

}