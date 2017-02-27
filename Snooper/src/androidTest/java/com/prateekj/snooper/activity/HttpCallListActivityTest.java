package com.prateekj.snooper.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.prateekj.snooper.R;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_OK;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.utils.EspressoActions.withRecyclerView;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class HttpCallListActivityTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Rule
  public IntentsTestRule<HttpCallListActivity> activityRule =
      new IntentsTestRule<>(HttpCallListActivity.class, true, false);
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(RealmFactory.create(getTargetContext()));
  }

  @Test
  public void shouldRenderHttpCalls() throws Exception {
    saveHttpCall("https://www.google.com", "GET", 200, "OK");
    saveHttpCall("https://www.facebook.com", "GET", 200, "OK");

    activityRule.launchActivity(null);

    onView(withRecyclerView(R.id.list, 0)).check(matches(allOf(
        hasDescendant(withText("https://www.google.com")),
        hasDescendant(withText("GET")),
        hasDescendant(withText("200")),
        hasDescendant(withText("OK"))
    )));

    onView(withRecyclerView(R.id.list, 1)).check(matches(allOf(
        hasDescendant(withText("https://www.facebook.com")),
        hasDescendant(withText("GET")),
        hasDescendant(withText("200")),
        hasDescendant(withText("OK"))
    )));

    verifyClickActionOnListItem(0, 1);
    verifyClickActionOnListItem(1, 2);
  }

  private void verifyClickActionOnListItem(int itemIndex, int httpCallId) {
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withRecyclerView(R.id.list, itemIndex)).perform(click());
    intended(allOf(
        hasComponent(ResponseBodyActivity.class.getName()),
        hasExtra(ResponseBodyActivity.HTTP_CALL_ID, httpCallId)));
  }

  private void saveHttpCall(String url, String method, int statusCode, String statusText) {
    HttpCall httpCall = new HttpCall.HttpCallBuilder()
        .withUrl(url)
        .withMethod(method)
        .withStatusCode(statusCode)
        .withStatusText(statusText)
        .build();
    snooperRepo.save(httpCall);
  }
}