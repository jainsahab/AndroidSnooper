package com.prateekj.snooper.viewmodel;

import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.prateekj.snooper.viewmodel.HttpHeaderViewModel.toViewModels;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
    httpHeaderViewModel = new HttpHeaderViewModel(httpHeader, 0);
  }

  @Test
  public void shouldReturnHeaderName() throws Exception {
    assertThat(httpHeaderViewModel.headerName(), is("accept-language"));
  }

  @Test
  public void shouldReturnHeaderValues() throws Exception {
    assertThat(httpHeaderViewModel.headerValues(), is("en-US,en;q=0.8,hi;q=0.6"));
  }

  @Test
  public void shouldReturnViewModels() throws Exception {
    HttpHeader header1 = new HttpHeader("accept-language");
    header1.setValues(singletonList(new HttpHeaderValue("en-US,en")));
    HttpHeader header2 = new HttpHeader("content-type");
    header2.setValues(singletonList(new HttpHeaderValue("application/json")));

    List<HttpHeaderViewModel> viewModels = toViewModels(asList(header1, header2), 0);

    assertThat(viewModels.get(0).headerName(), is("accept-language"));
    assertThat(viewModels.get(0).headerValues(), is("en-US,en"));
    assertThat(viewModels.get(1).headerName(), is("content-type"));
    assertThat(viewModels.get(1).headerValues(), is("application/json"));
  }
}