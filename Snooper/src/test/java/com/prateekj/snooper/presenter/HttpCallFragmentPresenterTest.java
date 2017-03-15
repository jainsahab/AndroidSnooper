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
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.views.HttpCallBodyView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallFragmentPresenterTest {

  private HttpCallFragmentPresenter presenter;
  private SnooperRepo repo;
  private HttpBodyViewModel viewModel;
  public static final int HTTP_CALL_ID = 5;
  private HttpCall httpCall;
  private ResponseFormatterFactory factory;
  private ResponseFormatter responseFormatter;
  private String responseBody;
  private String requestPayload;
  private String formattedBody;
  private BackgroundTaskExecutor mockExecutor;
  private HttpCallBodyView httpCallBodyView;

  @Before
  public void setUp() throws Exception {
    responseBody = "response body";
    requestPayload = "payload";
    formattedBody = "formatted body";
    httpCall = mock(HttpCall.class);
    repo = mock(SnooperRepo.class);
    viewModel = mock(HttpBodyViewModel.class);
    factory = mock(ResponseFormatterFactory.class);
    mockExecutor = Mockito.mock(BackgroundTaskExecutor.class);
    httpCallBodyView = mock(HttpCallBodyView.class);
    presenter = new HttpCallFragmentPresenter(repo, HTTP_CALL_ID, httpCallBodyView, factory, mockExecutor);
    responseFormatter = mock(ResponseFormatter.class);
    when(httpCall.getResponseBody()).thenReturn(responseBody);
    when(httpCall.getPayload()).thenReturn(requestPayload);
    when(repo.findById(HTTP_CALL_ID)).thenReturn(httpCall);
    when(factory.getFor(anyString())).thenReturn(responseFormatter);
    when(responseFormatter.format(anyString())).thenReturn(formattedBody);
  }

  @Test
  public void shouldInitializeWithJsonFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, RESPONSE_MODE);

    verify(factory).getFor("application/json");
    verify(responseFormatter).format(responseBody);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithXmlFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, RESPONSE_MODE);

    verify(factory).getFor("application/xml");
    verify(responseFormatter).format(responseBody);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithJsonFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(factory).getFor("application/json");
    verify(responseFormatter).format(requestPayload);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithXmlFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(factory).getFor("application/xml");
    verify(responseFormatter).format(requestPayload);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldNotifyViewOnFormattingDone() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(httpCallBodyView).onFormattingDone();
  }

  private void resolveBackgroundTask() {
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        BackgroundTask<String> backgroundTask = (BackgroundTask<String>) invocation.getArguments()[0];
        backgroundTask.onResult(backgroundTask.onExecute());
        return null;
      }
    }).when(mockExecutor).execute(any(BackgroundTask.class));
  }

  @NonNull
  private HttpHeader getJsonContentTypeHeader() {
    HttpHeaderValue headerValue = new HttpHeaderValue("application/json");
    HttpHeader httpHeader = new HttpHeader("Content-Type");
    httpHeader.setValues(Collections.singletonList(headerValue));
    return httpHeader;
  }

  @NonNull
  private HttpHeader getXmlContentTypeHeader() {
    HttpHeaderValue headerValue = new HttpHeaderValue("application/xml");
    HttpHeader httpHeader = new HttpHeader("Content-Type");
    httpHeader.setValues(Collections.singletonList(headerValue));
    return httpHeader;
  }
}
