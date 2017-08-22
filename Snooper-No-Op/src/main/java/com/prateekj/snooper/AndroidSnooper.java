package com.prateekj.snooper;

import android.app.Application;

import com.prateekj.snooper.networksnooper.model.HttpCall;

import java.io.IOException;

public class AndroidSnooper {

  public static final String ACTION_END_SNOOPER_FLOW = "com.snooper.END_SNOOPER_FLOW";

  private static AndroidSnooper androidSnooper;

  private AndroidSnooper() {
  }

  public void record(final HttpCall httpCall) throws IOException {
  }

  public static AndroidSnooper init(Application application) {
    if (androidSnooper == null) {
      androidSnooper = new AndroidSnooper();
    }
    return androidSnooper;
  }

  public static AndroidSnooper getInstance() {
    if (androidSnooper == null) {
      throw new RuntimeException("Android Snooper is not initialized yet");
    }
    return androidSnooper;
  }
}
