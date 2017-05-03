package com.prateekj.snooper.networksnooper.viewmodel;

import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;
import com.prateekj.snooper.utils.TestUtilities;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class HttpCallViewModelTest {
  private HttpCall httpCall;
  private HttpCallViewModel httpCallViewModel;

  @Before
  public void setUp() throws Exception {
    final String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0";

    httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod("POST")
      .withStatusCode(200)
      .withStatusText("OK")
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
    assertTrue(httpCallViewModel.getStatusCode() == httpCall.getStatusCode());
  }

  @Test
  public void getStatusText() throws Exception {
    assertTrue(httpCallViewModel.getStatusText().equals(httpCall.getStatusText()));
  }

  @Test
  public void getTimeStamp() throws Exception {
    assertTrue(httpCallViewModel.getTimeStamp().equals("06/02/2017 11:22:33"));
  }
}