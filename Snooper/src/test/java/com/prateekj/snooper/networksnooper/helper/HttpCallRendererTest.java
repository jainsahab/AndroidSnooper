package com.prateekj.snooper.networksnooper.helper;

import androidx.fragment.app.Fragment;

import com.prateekj.snooper.networksnooper.activity.HttpCallTab;
import com.prateekj.snooper.networksnooper.views.HttpCallView;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.ERROR;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpCallRendererTest {

  private HttpCallView httpCallView;
  private HttpCallRenderer httpCallRenderer;

  @Before
  public void setUp() throws Exception {
    httpCallView = mock(HttpCallView.class);
    httpCallRenderer = new HttpCallRenderer(httpCallView, false);
  }

  @Test
  public void shouldReturnResponseTypeFragment() throws Exception {
    Fragment expectedFragment = mock(Fragment.class);
    when(httpCallView.getResponseBodyFragment()).thenReturn(expectedFragment);

    Fragment fragment = httpCallRenderer.getFragment(0);

    assertThat(fragment, sameInstance(expectedFragment));
    verify(httpCallView).getResponseBodyFragment();
  }

  @Test
  public void shouldReturnErrorTypeFragment() throws Exception {
    Fragment expectedFragment = mock(Fragment.class);
    when(httpCallView.getExceptionFragment()).thenReturn(expectedFragment);
    HttpCallRenderer httpCallRenderer = new HttpCallRenderer(httpCallView, true);

    Fragment fragment = httpCallRenderer.getFragment(0);

    assertThat(fragment, sameInstance(expectedFragment));
    verify(httpCallView).getExceptionFragment();
  }

  @Test
  public void shouldReturnRequestTypeFragment() throws Exception {
    Fragment expectedFragment = mock(Fragment.class);
    when(httpCallView.getRequestBodyFragment()).thenReturn(expectedFragment);

    Fragment fragment = httpCallRenderer.getFragment(1);

    assertThat(fragment, sameInstance(expectedFragment));
    verify(httpCallView).getRequestBodyFragment();
  }

  @Test
  public void shouldReturnHeaderTypeFragment() throws Exception {
    Fragment expectedFragment = mock(Fragment.class);
    when(httpCallView.getHeadersFragment()).thenReturn(expectedFragment);

    Fragment fragment = httpCallRenderer.getFragment(2);

    assertThat(fragment, sameInstance(expectedFragment));
    verify(httpCallView).getHeadersFragment();
  }

  @Test
  public void shouldReturnHttpCallTabs() throws Exception {
    List<HttpCallTab> tabs = httpCallRenderer.getTabs();

    assertThat(tabs.get(0), is(RESPONSE));
    assertThat(tabs.get(1), is(REQUEST));
    assertThat(tabs.get(2), is(HEADERS));
  }

  @Test
  public void shouldReturnHttpCallTabsForErrorMode() throws Exception {
    HttpCallRenderer httpCallRenderer = new HttpCallRenderer(httpCallView, true);
    List<HttpCallTab> tabs = httpCallRenderer.getTabs();

    assertThat(tabs.get(0), is(ERROR));
    assertThat(tabs.get(1), is(REQUEST));
    assertThat(tabs.get(2), is(HEADERS));
  }
}