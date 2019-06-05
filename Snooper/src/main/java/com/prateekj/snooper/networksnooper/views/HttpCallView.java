package com.prateekj.snooper.networksnooper.views;


import androidx.fragment.app.Fragment;

public interface HttpCallView {
  void copyToClipboard(String data);
  void shareData(String completeHttpCallData);
  void showMessageShareNotAvailable();
  Fragment getRequestBodyFragment();
  Fragment getResponseBodyFragment();
  Fragment getHeadersFragment();
  Fragment getExceptionFragment();
}
