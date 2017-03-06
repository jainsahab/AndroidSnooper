package com.prateekj.snooper.activity;

import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.ImmutableMap;
import android.support.test.rule.ActivityTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.allOf;

public class HttpCallActivityTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Rule
  public ActivityTestRule<HttpCallActivity> activityRule =
    new ActivityTestRule<>(HttpCallActivity.class, true, false);
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(rule.getRealm());
  }

  @Test
  public void shouldRenderRequestAndResponseBody() throws Exception {
    String responseBody = readFrom("person_details_raw_response.json");
    String requestPayload = readFrom("person_details_raw_request.json");
    saveHttpCall("https://www.abc.com/person/1", "GET", 200, "OK", responseBody, requestPayload);

    Intent intent = new Intent();
    intent.putExtra(HTTP_CALL_ID, 1);

    activityRule.launchActivity(intent);

    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_response.json"))));

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click());

    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))));
  }

  private void saveHttpCall(String url, String method,
                            int statusCode, String statusText, String responseBody, String requestPayload) {
    Map<String, List<String>> headers = ImmutableMap.of("Content-Type", singletonList("application/json"));
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withStatusCode(statusCode)
      .withStatusText(statusText)
      .withResponseBody(responseBody)
      .withPayload(requestPayload)
      .withRequestHeaders(headers)
      .withResponseHeaders(headers)
      .build();
    snooperRepo.save(httpCall);
  }
}
