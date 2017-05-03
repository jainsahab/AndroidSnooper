package com.prateekj.snooper.networksnooper.viewmodel;

import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpHeaderViewModelTest {

  private HttpHeaderViewModel httpHeaderViewModel;

  @Before
  public void setUp() throws Exception {
    HttpHeader httpHeader = new HttpHeader("accept-language");
    HttpHeaderValue value1 = new HttpHeaderValue("en-US,en");
    HttpHeaderValue value2 = new HttpHeaderValue("q=0.8,hi");
    HttpHeaderValue value3 = new HttpHeaderValue("q=0.6");
    httpHeader.setValues(asList(value1, value2, value3));
    httpHeaderViewModel = new HttpHeaderViewModel(httpHeader);
  }

  @Test
  public void shouldReturnHeaderName() throws Exception {
    assertThat(httpHeaderViewModel.headerName(), is("accept-language"));
  }

  @Test
  public void shouldReturnHeaderValues() throws Exception {
    assertThat(httpHeaderViewModel.headerValues(), is("en-US,en;q=0.8,hi;q=0.6"));
  }
}