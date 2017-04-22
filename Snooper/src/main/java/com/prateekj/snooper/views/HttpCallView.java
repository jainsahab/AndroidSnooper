package com.prateekj.snooper.views;

public interface HttpCallView {
  void copyToClipboard(String data);
  void shareData(StringBuilder completeHttpCallData);
  void onHttpCallBodyFormatted(int mode);

  void dismissProgressDialog();
}
