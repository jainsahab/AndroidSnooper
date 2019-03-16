package com.prateekj.snooper.utils;

import android.util.Log;

public class Logger {
  private static final String SNOOPER_DEBUGGER_TAG = "AndroidSnooper";

  public static void d(String tag, String message) {
    Log.d(SNOOPER_DEBUGGER_TAG + tag, message);
  }

  public static void e(String tag, String message, Throwable exception) {
    Log.e(SNOOPER_DEBUGGER_TAG + tag, message, exception);
  }

  public static void e(String tag, String message) {
    Log.e(SNOOPER_DEBUGGER_TAG + tag, message);
  }
}
