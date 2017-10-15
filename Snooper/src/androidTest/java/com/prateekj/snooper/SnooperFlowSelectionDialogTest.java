package com.prateekj.snooper;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.dbreader.activity.DatabaseListActivity;
import com.prateekj.snooper.networksnooper.activity.HttpCallListActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.prateekj.snooper.SnooperFlowSelectionDialog.DEFAULT_FRAGMENT_TAG;

public class SnooperFlowSelectionDialogTest {

  @Rule
  public IntentsTestRule<TestActivity> activityRule = new IntentsTestRule<>(TestActivity.class, true, false);

  @Test
  public void shouldRenderAllSnooperFlows() throws Throwable {
    activityRule.launchActivity(null);

    showDialog();
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withId(R.id.network_history_selection)).perform(click());
    intended(hasComponent(HttpCallListActivity.class.getName()));
    verifyDialogIsDismissed();

    showDialog();
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withId(R.id.database_reader_selection)).perform(click());
    intended(hasComponent(DatabaseListActivity.class.getName()));
    verifyDialogIsDismissed();

    showDialog();
    onView(withId(R.id.close_icon)).perform(click());
    verifyDialogIsDismissed();
  }

  private void showDialog() throws Throwable {
    activityRule.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        SnooperFlowSelectionDialog snooperFlowSelectionDialog = new SnooperFlowSelectionDialog();
        snooperFlowSelectionDialog.show(activityRule.getActivity().getFragmentManager(), DEFAULT_FRAGMENT_TAG);
      }
    });
  }

  private void verifyDialogIsDismissed() {
    onView(withId(R.id.network_history_selection)).check(doesNotExist());
    onView(withId(R.id.database_reader_selection)).check(doesNotExist());
  }
}