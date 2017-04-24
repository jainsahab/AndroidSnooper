package com.prateekj.snooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.utils.FileUtil;
import com.prateekj.snooper.views.HttpCallView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallPresenterTest {

  private ResponseFormatterFactory formatterFactory;
  private HttpCallView view;
  private HttpCall httpCall;
  private ResponseFormatter responseFormatter;
  private SnooperRepo repo;
  private FileUtil fileUtil;
  private BackgroundTaskExecutor backgroundTaskExecutor;

  @Before
  public void setUp() throws Exception {
    formatterFactory = mock(ResponseFormatterFactory.class);
    view = mock(HttpCallView.class);
    httpCall = mock(HttpCall.class);
    repo = mock(SnooperRepo.class);
    responseFormatter = mock(ResponseFormatter.class);
    fileUtil = mock(FileUtil.class);
    backgroundTaskExecutor = mock(BackgroundTaskExecutor.class);
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
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.copyHttpCallBody(0);
    verify(responseFormatter).format(responseBody);
    verify(view).copyToClipboard(formatResponseBody);
  }

  @Test
  public void shouldCopyEmptyStringIfContentHeaderIsMissingInResponseData() throws Exception {
    String responseBody = "response body";
    String expectedCopiedData = "";
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(null);
    when(httpCall.getResponseBody()).thenReturn(null);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

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
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.copyHttpCallBody(1);
    verify(responseFormatter).format(requestBody);
    verify(view).copyToClipboard(formatRequestBody);
  }

  @Test
  public void shouldCopyEmptyStringIfContentHeaderIsMissingInRequestData() throws Exception {
    String requestBody = "response body";
    String expectedRequestBody = "";
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(null);
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.copyHttpCallBody(1);
    verify(responseFormatter, never()).format(requestBody);
    verify(view).copyToClipboard(expectedRequestBody);
  }

  @Test
  public void shouldDismissDialogWhenRequestAndResponseDataHasBeenLoaded() throws Exception {
    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

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

  @Test
  public void shouldShareRequestResponseData() throws Exception {
    String requestBody = "request body";
    String formatRequestBody = "format Request body";
    String responseBody = "response body";
    String formatResponseBody = "format Response body";
    StringBuilder expectedData = new StringBuilder();
    expectedData.append("Request Body");
    expectedData.append("\n");
    expectedData.append(formatRequestBody);
    expectedData.append("\n");
    expectedData.append("Response Body");
    expectedData.append("\n");
    expectedData.append(formatResponseBody);

    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(requestBody)).thenReturn(formatRequestBody);
    when(responseFormatter.format(responseBody)).thenReturn(formatResponseBody);
    when(fileUtil.createLogFile(any(StringBuilder.class))).thenReturn("filePath");
    when(view.isWriteStoragePermissionGranted()).thenReturn(true);

    resolveBackgroundTask();

    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.shareHttpCallBody();
    verify(responseFormatter, times(1)).format(requestBody);
    verify(responseFormatter, times(1)).format(responseBody);
    verify(view).shareData("filePath");
  }

  @Test
  public void shouldNotShareDataIfFileNotCreated() throws Exception {
    String requestBody = "request body";
    String formatRequestBody = "format Request body";
    String responseBody = "response body";
    String formatResponseBody = "format Response body";
    StringBuilder expectedData = new StringBuilder();
    expectedData.append("Request Body");
    expectedData.append("\n");
    expectedData.append(formatRequestBody);
    expectedData.append("\n");
    expectedData.append("Response Body");
    expectedData.append("\n");
    expectedData.append(formatResponseBody);

    when(httpCall.getRequestHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getPayload()).thenReturn(requestBody);
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(getJsonContentTypeHeader());
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(formatterFactory.getFor("application/json")).thenReturn(responseFormatter);
    when(responseFormatter.format(requestBody)).thenReturn(formatRequestBody);
    when(responseFormatter.format(responseBody)).thenReturn(formatResponseBody);
    when(fileUtil.createLogFile(any(StringBuilder.class))).thenReturn("");
    when(view.isWriteStoragePermissionGranted()).thenReturn(true);

    resolveBackgroundTask();

    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.shareHttpCallBody();
    verify(responseFormatter, times(1)).format(requestBody);
    verify(responseFormatter, times(1)).format(responseBody);
    verify(view, never()).shareData("filePath");
  }

  @Test
  public void shouldNotShareDataIfPermissionNotGranted() throws Exception {
    String requestBody = "request body";
    String responseBody = "response body";

    doNothing().when(view).showMessageShareNotAvailable();
    when(view.isWriteStoragePermissionGranted()).thenReturn(false);

    resolveBackgroundTask();

    HttpCallPresenter httpCallPresenter = new HttpCallPresenter(1, repo, view, formatterFactory, fileUtil, backgroundTaskExecutor);

    httpCallPresenter.shareHttpCallBody();
    verify(view).isWriteStoragePermissionGranted();
    verify(httpCall, never()).getRequestHeader("Content-Type");
    verify(responseFormatter, never()).format(requestBody);
    verify(responseFormatter, never()).format(responseBody);
    verify(view, never()).shareData("filePath");
    verify(view).showMessageShareNotAvailable();
  }

  private void resolveBackgroundTask() {
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        BackgroundTask<String> backgroundTask = (BackgroundTask<String>) invocation.getArguments()[0];
        backgroundTask.onResult(backgroundTask.onExecute());
        return null;
      }
    }).when(backgroundTaskExecutor).execute(any(BackgroundTask.class));
  }

}