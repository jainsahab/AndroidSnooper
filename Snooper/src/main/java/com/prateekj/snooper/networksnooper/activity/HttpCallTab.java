package com.prateekj.snooper.networksnooper.activity;

import com.prateekj.snooper.R;

public enum HttpCallTab {
  RESPONSE(0, R.string.response),
  REQUEST(1, R.string.request),
  HEADERS(2, R.string.headers),
  ERROR(0, R.string.error);

  private int index;

  private int tabTitle;
  HttpCallTab(int index, int tabTitle) {
    this.index = index;
    this.tabTitle = tabTitle;
  }

  public int getIndex() {
    return index;
  }

  public int getTabTitle() {
    return tabTitle;
  }

  public static HttpCallTab byIndex(int index) {
    for (HttpCallTab tab : HttpCallTab.values()) {
      if (tab.getIndex() == index) {
        return tab;
      }
    }
    return null;
  }
}
