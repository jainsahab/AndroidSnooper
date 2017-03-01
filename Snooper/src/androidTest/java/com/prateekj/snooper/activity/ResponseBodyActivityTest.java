package com.prateekj.snooper.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.activity.ResponseBodyActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.utils.TestUtilities.readFrom;

public class ResponseBodyActivityTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Rule
  public ActivityTestRule<ResponseBodyActivity> activityRule =
      new ActivityTestRule<>(ResponseBodyActivity.class, true, false);
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(rule.getRealm());
  }

  @Test
  public void shouldRenderResponsePayload() throws Exception {
    String payload = readFrom("person_details_raw_response.json");
    saveHttpCall("https://www.abc.com/person/1", "GET", 200, "OK", payload);

    Intent intent = new Intent();
    intent.putExtra(HTTP_CALL_ID, 1);

    activityRule.launchActivity(intent);

    onView(withId(R.id.payload_text))
        .check(matches(withText(readFrom("person_details_formatted_response.json"))));
  }

  private void saveHttpCall(String url, String method,
                            int statusCode, String statusText, String payload) {
    HttpCall httpCall = new HttpCall.HttpCallBuilder()
        .withUrl(url)
        .withMethod(method)
        .withStatusCode(statusCode)
        .withStatusText(statusText)
        .withPayload(payload)
        .build();
    snooperRepo.save(httpCall);
  }
}