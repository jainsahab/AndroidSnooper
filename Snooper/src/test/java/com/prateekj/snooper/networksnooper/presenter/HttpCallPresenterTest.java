package com.prateekj.snooper.networksnooper.presenter;

import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.helper.DataCopyHelper;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.views.HttpCallView;
import com.prateekj.snooper.utils.FileUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.ERROR;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallPresenterTest {

  private HttpCallView view;
  private HttpCallRecord httpCall;
  private DataCopyHelper dataCopyHelper;
  private FileUtil fileUtil;
  private BackgroundTaskExecutor backgroundTaskExecutor;
  private HttpCallPresenter httpCallPresenter;

  @Before
  public void setUp() throws Exception {
    view = mock(HttpCallView.class);
    httpCall = mock(HttpCallRecord.class);
    dataCopyHelper = mock(DataCopyHelper.class);
    fileUtil = mock(FileUtil.class);
    backgroundTaskExecutor = mock(BackgroundTaskExecutor.class);
    httpCallPresenter = new HttpCallPresenter(dataCopyHelper, httpCall, view, fileUtil, backgroundTaskExecutor);
  }

  @Test
  public void shouldAskViewToCopyTheResponseData() throws Exception {
    String responseBody = "response body";
    when(dataCopyHelper.getResponseDataForCopy()).thenReturn(responseBody);

    httpCallPresenter.copyHttpCallBody(RESPONSE);

    verify(view).copyToClipboard(responseBody);
  }

  @Test
  public void shouldAskViewToCopyTheRequestData() throws Exception {
    String formatRequestBody = "format Request body";
    when(dataCopyHelper.getRequestDataForCopy()).thenReturn(formatRequestBody);

    httpCallPresenter.copyHttpCallBody(REQUEST);

    verify(view).copyToClipboard(formatRequestBody);
  }

  @Test
  public void shouldAskViewToCopyTheError() throws Exception {
    String error = "error";
    when(dataCopyHelper.getErrorsForCopy()).thenReturn(error);

    httpCallPresenter.copyHttpCallBody(ERROR);

    verify(view).copyToClipboard(error);
  }

  @Test
  public void shouldShareRequestResponseData() throws Exception {
    when(httpCall.getDate()).thenReturn(getDate(2017, 4, 12, 1, 2, 3));
    StringBuilder stringBuilder = new StringBuilder();
    when(dataCopyHelper.getHttpCallData()).thenReturn(stringBuilder);
    when(fileUtil.createLogFile(eq(stringBuilder), eq("2017_05_12_01_02_03.txt"))).thenReturn("filePath");
    resolveBackgroundTask();

    httpCallPresenter.shareHttpCallBody();

    verify(view).shareData("filePath");
  }

  @Test
  public void shouldNotShareDataIfFileNotCreated() throws Exception {
    when(httpCall.getDate()).thenReturn(getDate(2017, 4, 12, 1, 2, 3));
    StringBuilder stringBuilder = new StringBuilder();
    when(dataCopyHelper.getHttpCallData()).thenReturn(stringBuilder);
    when(fileUtil.createLogFile(eq(stringBuilder), eq("2017_05_12_01_02_03.txt"))).thenReturn("");
    resolveBackgroundTask();

    httpCallPresenter.shareHttpCallBody();

    verify(view, never()).shareData("filePath");
  }

  @Test
  public void shouldShowShareNotAvailableDialogWhenPermissionIsDenied() throws Exception {
    httpCallPresenter.onPermissionDenied();

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