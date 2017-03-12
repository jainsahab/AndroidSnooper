package com.prateekj.snooper.infra;

public interface BackgroundTask <E>{
  E onExecute();
  void onResult(E result);
}
