package com.prateekj.snooper.utility;

import java.io.UnsupportedEncodingException;

public class TestUtilities {

  public static byte[] toBytes(String string) {
    try {
      return string.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new byte[0];
  }
}
