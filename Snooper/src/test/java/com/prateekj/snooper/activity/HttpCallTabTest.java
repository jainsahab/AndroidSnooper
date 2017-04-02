package com.prateekj.snooper.activity;

import com.prateekj.snooper.R;

import org.junit.Test;

import java.util.List;

import static com.prateekj.snooper.activity.HttpCallTab.HEADERS;
import static com.prateekj.snooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.activity.HttpCallTab.RESPONSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class HttpCallTabTest {
  @Test
  public void shouldReturnResponseTabDetails() throws Exception {
    HttpCallTab tab = RESPONSE;
    assertThat(tab.getIndex(), is(0));
    assertThat(tab.getTabTitle(), is(R.string.response));
  }

  @Test
  public void shouldReturnRequestTabDetails() throws Exception {
    HttpCallTab tab = REQUEST;
    assertThat(tab.getIndex(), is(1));
    assertThat(tab.getTabTitle(), is(R.string.request));
  }

  @Test
  public void shouldReturnHeadersTabDetails() throws Exception {
    HttpCallTab tab = HEADERS;
    assertThat(tab.getIndex(), is(2));
    assertThat(tab.getTabTitle(), is(R.string.headers));
  }

  @Test
  public void shouldReturnTabByIndex() throws Exception {
    assertThat(HttpCallTab.byIndex(0), is(RESPONSE));
    assertThat(HttpCallTab.byIndex(1), is(REQUEST));
    assertThat(HttpCallTab.byIndex(2), is(HEADERS));
    assertNull(HttpCallTab.byIndex(3));
  }

  @Test
  public void shouldReturnSortedTabs() throws Exception {
    List<HttpCallTab> values = HttpCallTab.sortedValues();
    assertThat(values.size(), is(3));
    assertThat(values.get(0), is(RESPONSE));
    assertThat(values.get(1), is(REQUEST));
    assertThat(values.get(2), is(HEADERS));
  }
}