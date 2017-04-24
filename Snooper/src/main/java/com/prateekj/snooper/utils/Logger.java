package com.prateekj.snooper.utils;

import android.util.Log;

public class Logger {
  public static void d(String tag, String message) {
    Log.d(tag, message);
  }

  public static void e(String tag, String message, Throwable exception) {
    Log.e(tag, message, exception);
  }
}
