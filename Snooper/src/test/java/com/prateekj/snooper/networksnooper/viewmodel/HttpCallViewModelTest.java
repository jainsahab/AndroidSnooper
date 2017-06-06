package com.prateekj.snooper.networksnooper.viewmodel;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCall.Builder;
import com.prateekj.snooper.utils.TestUtilities;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpCallViewModelTest {
  private HttpCall httpCall;
  private HttpCallViewModel httpCallViewModel;

  @Before
  public void setUp() throws Exception {
    final String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0";

    httpCall = new Builder()
      .withUrl(url)
      .withMethod("POST")
      .withStatusCode(200)
      .withStatusText("OK")
      .withResponseHeaders(new HashMap<String, List<String>>())
      .withRequestHeaders(new HashMap<String, List<String>>())
      .build();

    Date currentDate = TestUtilities.getDate(2017, 5, 2, 11, 22, 33);
    httpCall.setDate(currentDate);

    httpCallViewModel = new HttpCallViewModel(httpCall);
  }

  @Test
  public void getUrl() throws Exception {
    assertTrue(httpCallViewModel.getUrl().equals(httpCall.getUrl()));
  }

  @Test
  public void getMethod() throws Exception {
    assertTrue(httpCallViewModel.getMethod().equals(httpCall.getMethod()));
  }

  @Test
  public void getStatusCode() throws Exception {
    assertThat(httpCallViewModel.getStatusCode(),  is("200"));
  }

  @Test
  public void getStatusText() throws Exception {
    assertTrue(httpCallViewModel.getStatusText().equals(httpCall.getStatusText()));
  }

  @Test
  public void getTimeStamp() throws Exception {
    assertTrue(httpCallViewModel.getTimeStamp().equals("06/02/2017 11:22:33"));
  }

  @Test
  public void getRequestHeaders() throws Exception {
    assertThat(httpCallViewModel.getRequestHeaders(), sameInstance(httpCall.getRequestHeaders()));
  }

  @Test
  public void getResponseHeaders() throws Exception {
    assertThat(httpCallViewModel.getResponseHeaders(), sameInstance(httpCall.getResponseHeaders()));
  }

  @Test
  public void shouldGetColorGreenWhenStatusCode2xx() {
    assertEquals(httpCallViewModel.getStatusColor(), R.color.snooper_green);
  }

  @Test
  public void shouldGetResponseHeaderVisibilityAsGone() {
    assertThat(httpCallViewModel.getResponseHeaderVisibility(), is(GONE));
  }

  @Test
  public void shouldGetRequestHeaderVisibilityAsGone() {
    assertThat(httpCallViewModel.getRequestHeaderVisibility(), is(GONE));
  }

  @Test
  public void shouldGetColorYellowWhenStatusCode3xx() {
    HttpCall httpCall = new Builder()
      .withUrl(" url 1")
      .withMethod("POST")
      .withStatusCode(302)
      .withStatusText("FAIL")
      .build();
    HttpCallViewModel viewModel = new HttpCallViewModel(httpCall);
    assertEquals(viewModel.getStatusColor(), R.color.snooper_yellow);
  }

  @Test
  public void shouldGetColorRedWhenStatusCode4xx() {
    HttpCall httpCall = new Builder()
      .withUrl(" url 1")
      .withMethod("POST")
      .withStatusCode(400)
      .withStatusText("FAIL")
      .build();
    HttpCallViewModel viewModel = new HttpCallViewModel(httpCall);
    assertEquals(viewModel.getStatusColor(), R.color.snooper_red);
  }

  @Test
  public void shouldReturnResponseInfoContainerVisibilityAsVisible() throws Exception {
    assertThat(httpCallViewModel.getResponseInfoVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldReturnResponseInfoContainerVisibilityAsGone() throws Exception {
    HttpCall httpCall = new Builder().withError("error").build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
    assertThat(httpCallViewModel.getResponseInfoVisibility(), is(GONE));
  }

  @Test
  public void shouldReturnFailedTextVisibilityAsGone() throws Exception {
    assertThat(httpCallViewModel.getFailedTextVisibility(), is(GONE));
  }

  @Test
  public void shouldReturnFailedTextVisibilityAsVisible() throws Exception {
    HttpCall httpCall = new Builder().withError("error").build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
    assertThat(httpCallViewModel.getFailedTextVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldGetResponseHeaderVisibilityAsVisible() {
    HttpCall httpCall = new Builder().withResponseHeaders(singleHeader()).build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
    assertThat(httpCallViewModel.getResponseHeaderVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldGetRequestHeaderVisibilityAsVisible() {
    HttpCall httpCall = new Builder().withRequestHeaders(singleHeader()).build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
    assertThat(httpCallViewModel.getRequestHeaderVisibility(), is(VISIBLE));
  }

  private HashMap<String, List<String>> singleHeader() {
    return new HashMap<String, List<String>>() {{
      put("header1", Arrays.asList("headerValue"));
    }};
  }
}