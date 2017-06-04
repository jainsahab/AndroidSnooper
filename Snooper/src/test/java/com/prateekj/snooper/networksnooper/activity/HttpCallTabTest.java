package com.prateekj.snooper.networksnooper.activity;

import com.prateekj.snooper.R;

import org.junit.Test;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpCallTabTest {
  @Test
  public void shouldReturnResponseTabDetails() throws Exception {
    assertThat(RESPONSE.getTabTitle(), is(R.string.response));
  }

  @Test
  public void shouldReturnRequestTabDetails() throws Exception {
    assertThat(REQUEST.getTabTitle(), is(R.string.request));
  }

  @Test
  public void shouldReturnHeadersTabDetails() throws Exception {
    assertThat(HEADERS.getTabTitle(), is(R.string.headers));
  }
}