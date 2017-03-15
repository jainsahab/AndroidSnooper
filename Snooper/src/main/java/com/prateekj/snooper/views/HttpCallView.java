package com.prateekj.snooper.views;

public interface HttpCallView {
  void copyToClipboard(String data);
  void onHttpCallBodyFormatted(int mode);
  void dismissProgressDialog();
}
