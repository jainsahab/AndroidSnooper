package com.prateekj.snooper;

import android.app.Application;

public class TestApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AndroidSnooper.init(this);
  }
}
