package com.prateekj.snooper.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.support.test.InstrumentationRegistry.getContext;

public class TestUtilities {

  public static InputStream readFileAsStream(String assetFileName) throws IOException {
    return getContext().getAssets().open(assetFileName);
  }

  public static String readFrom(String assetFileName) throws IOException {
    InputStream inputStream = readFileAsStream(assetFileName);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String input = "";
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      input += line + "\n";
    }
    return input.substring(0, input.length() - 1);
  }
}
