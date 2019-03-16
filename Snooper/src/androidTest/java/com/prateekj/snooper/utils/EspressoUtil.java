package com.prateekj.snooper.utils;


import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EspressoUtil {
  private final static int DEFAULT_WAIT_TIME = 1500;

  public static void waitFor(Condition condition) {
    waitFor(condition, DEFAULT_WAIT_TIME);
  }

  public static void waitFor(Condition condition, long timeout) {
    Logger.d(EspressoUtil.class.getSimpleName(), "Started waiting for condition");
    long endTime = new Date().getTime() + timeout;

    while (new Date().getTime() < endTime) {
      if (condition.isSatisfied()) {
        Logger.d(EspressoUtil.class.getSimpleName(), "Condition satisfied");
        return;
      }
      sleep();
    }

    String message = "Timed out waiting for condition to be satisfied!";
    Logger.e(EspressoUtil.class.getSimpleName(), message);
    throw new RuntimeException(message);
  }

  private static void sleep() {
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
