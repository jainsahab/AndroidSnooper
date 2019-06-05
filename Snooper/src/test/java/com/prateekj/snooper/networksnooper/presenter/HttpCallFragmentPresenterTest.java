package com.prateekj.snooper.networksnooper.presenter;

import androidx.annotation.NonNull;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.Bound;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;

import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.ERROR_MODE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.RESPONSE_MODE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class HttpCallFragmentPresenterTest {

  private HttpCallFragmentPresenter presenter;
  private SnooperRepo repo;
  private HttpBodyViewModel viewModel;
  public static final long HTTP_CALL_ID = 5;
  private HttpCallRecord httpCallRecord;
  private ResponseFormatterFactory factory;
  private ResponseFormatter responseFormatter;
  private String responseBody;
  private String requestPayload;
  private String error;
  private String formattedBody;
  private BackgroundTaskExecutor mockExecutor;
  private HttpCallBodyView httpCallBodyView;

  @Before
  public void setUp() throws Exception {
    responseBody = "response body";
    requestPayload = "payload";
    error = "error";
    formattedBody = "formatted body";
    httpCallRecord = mock(HttpCallRecord.class);
    repo = mock(SnooperRepo.class);
    viewModel = mock(HttpBodyViewModel.class);
    factory = mock(ResponseFormatterFactory.class);
    mockExecutor = Mockito.mock(BackgroundTaskExecutor.class);
    httpCallBodyView = mock(HttpCallBodyView.class);
    presenter = new HttpCallFragmentPresenter(repo, HTTP_CALL_ID, httpCallBodyView, factory, mockExecutor);
    responseFormatter = mock(ResponseFormatter.class);
    when(httpCallRecord.getResponseBody()).thenReturn(responseBody);
    when(httpCallRecord.getPayload()).thenReturn(requestPayload);
    when(httpCallRecord.getError()).thenReturn(error);
    when(repo.findById(HTTP_CALL_ID)).thenReturn(httpCallRecord);
    when(factory.getFor(anyString())).thenReturn(responseFormatter);
    when(responseFormatter.format(anyString())).thenReturn(formattedBody);
  }

  @Test
  public void shouldInitializeWithJsonFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCallRecord.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, RESPONSE_MODE);

    verify(factory).getFor("application/json");
    verify(responseFormatter).format(responseBody);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithXmlFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCallRecord.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, RESPONSE_MODE);

    verify(factory).getFor("application/xml");
    verify(responseFormatter).format(responseBody);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithErrorContent() throws Exception {
    resolveBackgroundTask();

    presenter.init(viewModel, ERROR_MODE);

    verify(viewModel).init(error);
  }

  @Test
  public void shouldInitializeWithJsonFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(factory).getFor("application/json");
    verify(responseFormatter).format(requestPayload);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldInitializeWithXmlFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(factory).getFor("application/xml");
    verify(responseFormatter).format(requestPayload);
    verify(viewModel).init(formattedBody);
  }

  @Test
  public void shouldUsePlainTextFormatterWhenContentTypeHeaderNotFound() throws Exception {
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(null);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verifyNoMoreInteractions(factory);
    verify(viewModel).init(requestPayload);
  }

  @Test
  public void shouldNotifyViewOnFormattingDone() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    verify(httpCallBodyView).onFormattingDone();
  }

  @Test
  public void shouldReturnBoundsToHighlight() throws Exception {
    when(responseFormatter.format(anyString())).thenReturn("ABC0124abc");
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    presenter.searchInBody("abc");

    verify(httpCallBodyView).removeOldHighlightedSpans();
    verify(httpCallBodyView).highlightBounds(argThat(new CustomTypeSafeMatcher<List<Bound>>("") {
      @Override
      protected boolean matchesSafely(List<Bound> item) {
        Bound firstBound = item.get(0);
        assertThat(firstBound.getLeft(), is(0));
        assertThat(firstBound.getRight(), is(3));
        Bound secondBound = item.get(1);
        assertThat(secondBound.getLeft(), is(7));
        assertThat(secondBound.getRight(), is(10));
        return true;
      }
    }));
  }

  @Test
  public void shouldNotHighlightSpansWhenPatternIsEmpty() throws Exception {
    when(responseFormatter.format(anyString())).thenReturn("ABC0124abc");
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    presenter.searchInBody("");

    verify(httpCallBodyView).removeOldHighlightedSpans();
    verify(httpCallBodyView, never()).highlightBounds(any(List.class));
  }

  @Test
  public void shouldNotHighlightSpansWhenPatternNotFound() throws Exception {
    when(responseFormatter.format(anyString())).thenReturn("ABC0124abc");
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCallRecord.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    resolveBackgroundTask();
    presenter.init(viewModel, REQUEST_MODE);

    presenter.searchInBody("789");

    verify(httpCallBodyView).removeOldHighlightedSpans();
    verify(httpCallBodyView, never()).highlightBounds(any(List.class));
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
