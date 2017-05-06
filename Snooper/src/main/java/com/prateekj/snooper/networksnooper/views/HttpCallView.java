package com.prateekj.snooper.networksnooper.views;

public interface HttpCallView {
  void copyToClipboard(String data);
  void shareData(String completeHttpCallData);
  void onHttpCallBodyFormatted(int mode);

  void dismissProgressDialog();

  void showMessageShareNotAvailable();
}
