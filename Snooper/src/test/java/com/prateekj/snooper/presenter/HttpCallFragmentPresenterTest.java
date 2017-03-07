package com.prateekj.snooper.presenter;

import android.support.annotation.NonNull;

import com.prateekj.snooper.formatter.JsonResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.XmlFormatter;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.prateekj.snooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.activity.HttpCallActivity.RESPONSE_MODE;
import static com.prateekj.snooper.utils.TestUtilities.withConcreteClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallFragmentPresenterTest {

  private HttpCallFragmentPresenter presenter;
  private SnooperRepo repo;
  private HttpBodyViewModel viewModel;
  public static final int HTTP_CALL_ID = 5;
  private HttpCall httpCall;

  @Before
  public void setUp() throws Exception {
    httpCall = mock(HttpCall.class);
    repo = mock(SnooperRepo.class);
    viewModel = mock(HttpBodyViewModel.class);
    when(repo.findById(HTTP_CALL_ID)).thenReturn(httpCall);

    presenter = new HttpCallFragmentPresenter(repo, HTTP_CALL_ID);
  }

  @Test
  public void shouldInitializeViewModelWithProperMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    presenter.init(viewModel, RESPONSE_MODE);

    verify(repo).findById(HTTP_CALL_ID);
    verify(viewModel).init(eq(httpCall), any(ResponseFormatter.class), eq(RESPONSE_MODE));
  }

  @Test
  public void shouldInitializeWithJsonFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    presenter.init(viewModel, RESPONSE_MODE);

    verify(viewModel).init(eq(httpCall), any(JsonResponseFormatter.class), eq(RESPONSE_MODE));
  }

  @Test
  public void shouldInitializeWithXmlFormatterForResponseMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCall.getResponseHeader("Content-Type")).thenReturn(httpHeader);
    presenter.init(viewModel, RESPONSE_MODE);

    verify(viewModel).init(eq(httpCall), argThat(withConcreteClass(XmlFormatter.class)), eq(RESPONSE_MODE));
  }

  @Test
  public void shouldInitializeWithJsonFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getJsonContentTypeHeader();
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    presenter.init(viewModel, REQUEST_MODE);

    verify(viewModel).init(eq(httpCall), any(JsonResponseFormatter.class), eq(REQUEST_MODE));
  }

  @Test
  public void shouldInitializeWithXmlFormatterForRequestMode() throws Exception {
    HttpHeader httpHeader = getXmlContentTypeHeader();
    when(httpCall.getRequestHeader("Content-Type")).thenReturn(httpHeader);
    presenter.init(viewModel, REQUEST_MODE);

    verify(viewModel).init(eq(httpCall), argThat(withConcreteClass(XmlFormatter.class)), eq(REQUEST_MODE));
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
