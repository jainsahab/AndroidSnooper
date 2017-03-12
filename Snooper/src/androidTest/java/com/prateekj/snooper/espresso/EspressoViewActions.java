package com.prateekj.snooper.espresso;

import android.support.annotation.NonNull;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.HumanReadables;
import android.view.View;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static java.lang.String.format;

public class EspressoViewActions {
  public static ViewAction waitFor(final Matcher<View> viewMatcher) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return anyView();
      }

      @Override
      public String getDescription() {
        return format("performing wait for until view matcher %s matches", viewMatcher.toString());
      }

      @Override
      public void perform(UiController uiController, View view) {
        ViewMatcherIdlingResource idlingResource = new ViewMatcherIdlingResource(5000, viewMatcher, view);
        registerIdlingResources(idlingResource);
        uiController.loopMainThreadUntilIdle();
        unregisterIdlingResources(idlingResource);
        if(!idlingResource.isMatched()) {
          throw new PerformException.Builder()
            .withActionDescription(getDescription())
            .withViewDescription(HumanReadables.getViewHierarchyErrorMessage(view, null, "Action timed out : " + getDescription(), null))
            .build();
        }
      }

      @NonNull
      private CustomTypeSafeMatcher<View> anyView() {
        return new CustomTypeSafeMatcher<View>("") {
          @Override
          protected boolean matchesSafely(View item) {
            return true;
          }
        };
      }
    };
  }
}
