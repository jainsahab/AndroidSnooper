package com.prateekj.snooper.utils;

import com.prateekj.snooper.formatter.ResponseFormatter;

import org.hamcrest.CustomTypeSafeMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

public class TestUtilities {
  public static CustomTypeSafeMatcher<ResponseFormatter> withConcreteClass(final Class<? extends ResponseFormatter> clazz) {
    return new CustomTypeSafeMatcher<ResponseFormatter>("Matches exact class " + clazz.getName()) {
      @Override
      protected boolean matchesSafely(ResponseFormatter object) {
        return object.getClass().getName().equals(clazz.getName());
      }
    };
  }

  public static Date getDate(int year, int month, int day, int hour, int minute, int seconds) {
    Calendar instance = Calendar.getInstance();
    instance.set(year, month, day, hour, minute, seconds);
    return instance.getTime();
  }
}
