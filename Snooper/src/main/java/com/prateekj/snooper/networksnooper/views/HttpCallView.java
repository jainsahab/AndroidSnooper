package com.prateekj.snooper.networksnooper.views;


import android.support.v4.app.Fragment;

public interface HttpCallView {
  void copyToClipboard(String data);
  void shareData(String completeHttpCallData);
  void showMessageShareNotAvailable();
  Fragment getRequestBodyFragment();
  Fragment getResponseBodyFragment();
  Fragment getHeadersFragment();
  Fragment getExceptionFragment();
}
