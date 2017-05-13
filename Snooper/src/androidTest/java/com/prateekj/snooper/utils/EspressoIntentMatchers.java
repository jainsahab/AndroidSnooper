package com.prateekj.snooper.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import static android.content.Intent.EXTRA_INTENT;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_SUBJECT;

public class EspressoIntentMatchers {

  public static Matcher<Bundle> forMailChooserIntent(final String action, final String mimeType, final String extraData, final String fileName) {
    return new CustomTypeSafeMatcher<Bundle>("Custom matcher for matching mail chooser intent") {
      @Override
      protected boolean matchesSafely(Bundle item) {
        Intent intent = (Intent) item.get(EXTRA_INTENT);
        Uri uri = intent.getParcelableExtra(EXTRA_STREAM);

        return action.equals(intent.getAction()) &&
          mimeType.equals(intent.getType()) &&
          intent.getStringExtra(EXTRA_SUBJECT).equalsIgnoreCase(extraData) &&
          uri.getPath().endsWith(fileName);
      }
    };
  }

}
