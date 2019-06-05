package com.prateekj.snooper.networksnooper.activity;

import android.app.Instrumentation;
import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.rules.DataResetRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class HttpCallSearchActivityTest {
  @Rule
  public DataResetRule dataResetRule = new DataResetRule();

  @Rule
  public IntentsTestRule<HttpCallSearchActivity> activityRule =
    new IntentsTestRule<>(HttpCallSearchActivity.class, true, false);
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(getTargetContext());
  }

  @Test
  public void shouldRenderHttpCalls() throws Exception {
    Date beforeDate = getDate(2017, 5, 2, 11, 22, 33);
    Date afterDate = getDate(2017, 5, 3, 11, 22, 33);
    saveHttpCall("https://www.google.com", "GET", 200, "OK", beforeDate);
    long facebookCallId = saveHttpCall("https://www.facebook.com", "GET", 200, "OK", afterDate);

    activityRule.launchActivity(null);

    onView(withId(R.id.search_src_text)).perform(typeText("eeeeeee"));
    onView(withText("No results found for 'eeeeeee'")).check(matches(isDisplayed()));

    onView(withId(R.id.search_src_text)).perform(clearText(), typeText(".com"));
    onView(withRecyclerView(R.id.list, 0)).check(matches(allOf(
      hasDescendant(withText("https://www.facebook.com")),
      hasDescendant(withText("GET")),
      hasDescendant(withText("200")),
      hasDescendant(withText("OK")),
      hasDescendant(withText("06/03/2017 11:22:33"))
    )));

    onView(withRecyclerView(R.id.list, 1)).check(matches(allOf(
      hasDescendant(withText("https://www.google.com")),
      hasDescendant(withText("GET")),
      hasDescendant(withText("200")),
      hasDescendant(withText("OK")),
      hasDescendant(withText("06/02/2017 11:22:33"))
    )));

    onView(withId(R.id.search_src_text)).perform(clearText(), typeText("facebook"));
    verifyClickActionOnListItem(0, facebookCallId);

    assertTrue(activityRule.getActivity().isFinishing());
  }

  private void verifyClickActionOnListItem(int itemIndex, long httpCallId) {
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withRecyclerView(R.id.list, itemIndex)).perform(click());
    intended(allOf(
      hasComponent(HttpCallActivity.class.getName()),
      hasExtra(HTTP_CALL_ID, httpCallId)));
  }

  private long saveHttpCall(String url, String method, int statusCode, String statusText, Date date) {
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withStatusCode(statusCode)
      .withStatusText(statusText)
      .build();
    httpCall.setDate(date);
    return snooperRepo.save(HttpCallRecord.from(httpCall));
  }
}