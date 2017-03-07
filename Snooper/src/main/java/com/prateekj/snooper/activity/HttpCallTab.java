package com.prateekj.snooper.activity;

import com.prateekj.snooper.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum HttpCallTab {
  RESPONSE(0, R.string.response),
  REQUEST(1, R.string.request);

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

  public static List<HttpCallTab> sortedValues() {
    List<HttpCallTab> tabs = Arrays.asList(HttpCallTab.values());
    Collections.sort(tabs, new Comparator<HttpCallTab>() {
      @Override
      public int compare(HttpCallTab o1, HttpCallTab o2) {
        return o1.getIndex() - o2.getIndex();
      }
    });
    return tabs;
  }
}
