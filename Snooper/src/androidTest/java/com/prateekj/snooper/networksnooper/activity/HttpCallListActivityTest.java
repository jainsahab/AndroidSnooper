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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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
import static com.prateekj.snooper.utils.EspressoViewMatchers.withListSize;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class HttpCallListActivityTest {

  @Rule
  public DataResetRule dataResetRule = new DataResetRule();

  @Rule
  public IntentsTestRule<HttpCallListActivity> activityRule =
    new IntentsTestRule<>(HttpCallListActivity.class, true, false);
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(getTargetContext());
  }

  @Test
  public void shouldRenderHttpCalls() throws Exception {
    Date beforeDate = getDate(2017, 5, 2, 11, 22, 33);
    Date afterDate = getDate(2017, 5, 3, 11, 22, 33);
    long googleCallId = saveHttpCall("https://www.google.com", "GET", 200, "OK", beforeDate);
    long facebookCallId = saveHttpCall("https://www.facebook.com", "GET", 200, "OK", afterDate);

    activityRule.launchActivity(null);

    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()));
    onView(withText(R.string.done)).check(matches(isDisplayed()));
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

    verifyClickActionOnListItem(0, facebookCallId);
    verifyClickActionOnListItem(1, googleCallId);

    onView(withText(R.string.done)).perform(click());
    assertTrue(activityRule.getActivity().isFinishing());
  }

  @Test
  public void shouldRenderWithoutHttpCalls() throws Exception {
    activityRule.launchActivity(null);

    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()));
    onView(withText(R.string.done)).check(matches(isDisplayed()));
    onView(withText(R.string.no_calls_found)).check(matches(isDisplayed()));
    onView(withId(R.id.delete_records_menu)).check(doesNotExist());

    onView(withText(R.string.done)).perform(click());
    assertTrue(activityRule.getActivity().isFinishing());
  }

  @Test
  public void shouldVerifyDeleteBehaviorWhenDeleteTapped() throws Exception {
    long currentDateMillis = getDate(2017, 4, 1, 11, 22, 33).getTime();
    for (int index = 0; index < 50; index++) {
      saveHttpCall("https://www.facebook.com/" + index, "GET", 200, "OK", new Date(currentDateMillis + (index * 1000)));
    }
    saveErrorHttpCall("https://www.abc.com", "GET", getDate(2017, 5, 1, 11, 22, 33));
    saveHttpCall("https://www.google.com", "GET", 200, "OK", getDate(2017, 5, 2, 11, 22, 33));
    saveHttpCall("https://www.facebook.com", "GET", 200, "OK", getDate(2017, 5, 3, 11, 22, 33));

    activityRule.launchActivity(null);
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

    onView(withRecyclerView(R.id.list, 2)).check(matches(allOf(
      hasDescendant(withText("https://www.abc.com")),
      hasDescendant(withText("FAILED")),
      hasDescendant(withText("06/01/2017 11:22:33"))
    )));

    onView(withId(R.id.delete_records_menu)).perform(click());
    onView(withText(R.string.delete_records_dialog_cancellation)).perform(click());
    onView(withId(R.string.delete_records_dialog_text)).check(doesNotExist());

    onView(withId(R.id.delete_records_menu)).perform(click());
    onView(withText(R.string.delete_records_dialog_confirmation)).perform(click());
    onView(withText(R.string.title_activity_http_call_list)).check(matches(isDisplayed()));
    onView(withId(R.id.list)).check(matches(withListSize(0)));
  }

  private void verifyClickActionOnListItem(int itemIndex, long httpCallId) {
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withRecyclerView(R.id.list, itemIndex)).perform(click());
    intended(allOf(
      hasComponent(HttpCallActivity.class.getName()),
      hasExtra(HttpCallActivity.HTTP_CALL_ID, httpCallId)));
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

  private void saveErrorHttpCall(String url, String method, Date date) {
    String error = "java.net.ConnectException: failed to connect to localhost/127.0.0.1 " +
      "(port 80) after 30000ms: isConnected failed: ECONNREFUSED (Connection refused)";
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withError(error)
      .build();
    httpCall.setDate(date);
    snooperRepo.save(HttpCallRecord.from(httpCall));
  }
}
