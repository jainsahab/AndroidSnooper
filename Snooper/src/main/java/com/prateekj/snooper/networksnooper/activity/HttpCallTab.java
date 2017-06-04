package com.prateekj.snooper.networksnooper.activity;

import com.prateekj.snooper.R;

public enum HttpCallTab {
  RESPONSE(R.string.response),
  REQUEST(R.string.request),
  HEADERS(R.string.headers),
  ERROR(R.string.error);

  private int tabTitle;

  HttpCallTab(int tabTitle) {
    this.tabTitle = tabTitle;
  }

  public int getTabTitle() {
    return tabTitle;
  }
}
