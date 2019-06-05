package com.prateekj.snooper.espresso;

import androidx.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

public class EspressoViewActions {
  public static final int ONE_SECOND = 1000;
  public static ViewAction waitFor(final Matcher<View> viewMatcher) {
    return new WaitForViewAction(viewMatcher, ONE_SECOND * 5);
  }

  public static ViewAction waitFor(final Matcher<View> viewMatcher, int timeout) {
    return new WaitForViewAction(viewMatcher, timeout);
  }
}
