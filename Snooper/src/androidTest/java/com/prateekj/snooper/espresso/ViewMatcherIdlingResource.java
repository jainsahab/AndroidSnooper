package com.prateekj.snooper.espresso;

import androidx.test.espresso.IdlingResource;
import android.view.View;

import com.prateekj.snooper.utils.Logger;

import org.hamcrest.Matcher;

import static java.lang.Thread.sleep;

public class ViewMatcherIdlingResource implements IdlingResource {

  private static final int MATCHER_POLLING_INTERVAL_IN_MILLIS = 100;
  public static final String TAG = ViewMatcherIdlingResource.class.getSimpleName();
  private int waitTimeInMillis;
  private final Matcher<? super View> viewMatcher;
  private final View view;
  private ResourceCallback resourceCallback;
  private long startTime;
  private boolean matched;

  public ViewMatcherIdlingResource(final int waitTimeInMillis, final Matcher<? super View> viewMatcher, final View view) {
    this.waitTimeInMillis = waitTimeInMillis;
    this.viewMatcher = viewMatcher;
    this.view = view;
    matched = false;
  }

  @Override
  public String getName() {
    return this.getClass().getName();
  }

  @Override
  public boolean isIdleNow() {
    boolean timeExceeded = now() - startTime >= waitTimeInMillis;
    if(matched || timeExceeded) {
      resourceCallback.onTransitionToIdle();
    }
    return matched || timeExceeded;
  }

  public boolean isMatched() {
    return matched;
  }

  @Override
  public void registerIdleTransitionCallback(final ResourceCallback resourceCallback) {
    startTime = now();
    this.resourceCallback = resourceCallback;
    pollViewUntilMatchesOrTimeout(resourceCallback);
  }

  private void pollViewUntilMatchesOrTimeout(final ResourceCallback resourceCallback) {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (!viewMatcher.matches(view) && now() - startTime < waitTimeInMillis) {
          try {
            Logger.d(TAG, "polling view to match " + viewMatcher.toString());
            sleep(MATCHER_POLLING_INTERVAL_IN_MILLIS);
          } catch (InterruptedException ignored) {
          }
        }
        matched = viewMatcher.matches(view);
        resourceCallback.onTransitionToIdle();
      }
    });
    thread.start();
  }

  private long now() {
    return System.currentTimeMillis();
  }
}
