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
import static com.prateekj.snooper.networksnooper.model.HttpCallRecord.from;
import static org.hamcrest.CoreMatchers.is;
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
      .withResponseHeaders(singleHeader())
      .withRequestHeaders(singleHeader())
      .build();

    Date currentDate = TestUtilities.getDate(2017, 5, 2, 11, 22, 33);
    httpCall.setDate(currentDate);

    httpCallViewModel = new HttpCallViewModel(from(httpCall));
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
    assertThat(httpCallViewModel.getRequestHeaders().get(0).getName(), is("header1"));
  }

  @Test
  public void getResponseHeaders() throws Exception {
    assertThat(httpCallViewModel.getResponseHeaders().get(0).getName(), is("header1"));
  }

  @Test
  public void shouldGetColorGreenWhenStatusCode2xx() {
    assertEquals(httpCallViewModel.getStatusColor(), R.color.snooper_green);
  }

  @Test
  public void shouldGetResponseHeaderVisibilityAsGone() {
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(from(new Builder().build()));
    assertThat(httpCallViewModel.getResponseHeaderVisibility(), is(GONE));
  }

  @Test
  public void shouldGetRequestHeaderVisibilityAsGone() {
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(from(new Builder().build()));
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
    HttpCallViewModel viewModel = new HttpCallViewModel(from(httpCall));
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
    HttpCallViewModel viewModel = new HttpCallViewModel(from(httpCall));
    assertEquals(viewModel.getStatusColor(), R.color.snooper_red);
  }

  @Test
  public void shouldReturnResponseInfoContainerVisibilityAsVisible() throws Exception {
    assertThat(httpCallViewModel.getResponseInfoVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldReturnResponseInfoContainerVisibilityAsGone() throws Exception {
    HttpCall httpCall = new Builder().withError("error").build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(from(httpCall));
    assertThat(httpCallViewModel.getResponseInfoVisibility(), is(GONE));
  }

  @Test
  public void shouldReturnFailedTextVisibilityAsGone() throws Exception {
    assertThat(httpCallViewModel.getFailedTextVisibility(), is(GONE));
  }

  @Test
  public void shouldReturnFailedTextVisibilityAsVisible() throws Exception {
    HttpCall httpCall = new Builder().withError("error").build();
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(from(httpCall));
    assertThat(httpCallViewModel.getFailedTextVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldGetResponseHeaderVisibilityAsVisible() {
    assertThat(httpCallViewModel.getResponseHeaderVisibility(), is(VISIBLE));
  }

  @Test
  public void shouldGetRequestHeaderVisibilityAsVisible() {
    assertThat(httpCallViewModel.getRequestHeaderVisibility(), is(VISIBLE));
  }

  private HashMap<String, List<String>> singleHeader() {
    return new HashMap<String, List<String>>() {{
      put("header1", Arrays.asList("headerValue"));
    }};
  }
}