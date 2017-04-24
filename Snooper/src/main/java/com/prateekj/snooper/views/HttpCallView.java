package com.prateekj.snooper.views;

public interface HttpCallView {
  void copyToClipboard(String data);
  void shareData(String completeHttpCallData);
  void onHttpCallBodyFormatted(int mode);

  void dismissProgressDialog();
}
