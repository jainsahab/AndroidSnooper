package com.prateekj.snooper.utils;

import android.support.annotation.ColorRes;
import android.widget.TextView;

public class UIUtils {
  public static void setTextColor(TextView textView, @ColorRes int color) {
    textView.setTextColor(textView.getResources().getColor(color));
  }
}
