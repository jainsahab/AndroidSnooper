package com.prateekj.snooper.espresso;

import androidx.annotation.NonNull;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import android.view.View;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.registerIdlingResources;
import static androidx.test.espresso.Espresso.unregisterIdlingResources;
import static java.lang.String.format;

public class WaitForViewAction implements ViewAction {

  private Matcher<View> viewMatcher;
  private int timeout;

  public WaitForViewAction(Matcher<View> viewMatcher, int timeout) {
    this.viewMatcher = viewMatcher;
    this.timeout = timeout;
  }

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
    ViewMatcherIdlingResource idlingResource = new ViewMatcherIdlingResource(timeout, viewMatcher, view);
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
}
