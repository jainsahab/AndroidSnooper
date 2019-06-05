package com.prateekj.snooper.networksnooper.helper;


import androidx.fragment.app.Fragment;

import com.prateekj.snooper.networksnooper.activity.HttpCallTab;
import com.prateekj.snooper.networksnooper.views.HttpCallView;

import java.util.List;

import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.ERROR;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static java.util.Arrays.asList;

public class HttpCallRenderer {

  private HttpCallView httpCallView;
  private boolean hasError;

  public HttpCallRenderer(HttpCallView httpCallView, boolean hasError) {
    this.httpCallView = httpCallView;
    this.hasError = hasError;
  }

  public Fragment getFragment(int position) {
    if (position == 0 && this.hasError)
      return httpCallView.getExceptionFragment();
    if (position == 0)
      return httpCallView.getResponseBodyFragment();
    if (position == 1)
      return httpCallView.getRequestBodyFragment();
    return httpCallView.getHeadersFragment();

  }

  public List<HttpCallTab> getTabs() {
    if (hasError){
      return asList(ERROR, REQUEST, HEADERS);
    }
    return asList(RESPONSE, REQUEST, HEADERS);
  }
}