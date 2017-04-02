package com.prateekj.snooper.activity;

import android.content.ClipboardManager;
import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.ImmutableMap;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prateekj.snooper.R;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.rules.RealmCleanRule;
import com.prateekj.snooper.rules.RunUsingLooper;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.espresso.EspressoViewActions.ONE_SECOND;
import static com.prateekj.snooper.espresso.EspressoViewActions.waitFor;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

public class HttpCallActivityTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Rule
  public RunUsingLooper runUsingLooper = new RunUsingLooper();

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

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click());
    onView(withText("RESPONSE")).check(matches(isDisplayed())).perform(click());
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_response.json"))));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(readFrom("person_details_formatted_response.json")));

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click());
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(readFrom("person_details_formatted_request.json")));

    onView(withText("HEADERS")).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.response_headers)).perform(waitFor(hasItems(), ONE_SECOND * 20));

    verifyResponseHeader(0, "content-type", "application/json");
    verifyResponseHeader(1, "cache-control", "no-cache");
    verifyResponseHeader(2, "content-disposition", "attachment");
    verifyResponseHeader(3, "date", "Sun, 02 Apr 2017 08:54:39 GMT");

    verifyRequestHeader(0, "content-type", "application/json");
    verifyRequestHeader(1, "content-length", "403");
    verifyRequestHeader(2, "accept-language", "en-US,en;q=0.8,hi;q=0.6");
    verifyRequestHeader(3, ":scheme", "https");
  }

  private Matcher<View> hasItems() {
    return new CustomTypeSafeMatcher<View>("has item") {
      @Override
      protected boolean matchesSafely(View item) {
        if (!(item instanceof RecyclerView)) {
          return false;
        }
        RecyclerView recyclerView = (RecyclerView) item;
        return recyclerView.findViewHolderForAdapterPosition(0) != null;
      }
    };
  }

  private void verifyResponseHeader(int pos, String headerName, String headerValue) {
    onView(withRecyclerView(R.id.response_headers, pos)).check(matches(allOf(
      hasDescendant(withText(headerName)),
      hasDescendant(withText(headerValue))
    )));
  }

  private void verifyRequestHeader(int pos, String headerName, String headerValue) {
    onView(withRecyclerView(R.id.request_headers, pos)).check(matches(allOf(
      hasDescendant(withText(headerName)),
      hasDescendant(withText(headerValue))
    )));
  }

  private String clipBoardText() {
    ClipboardManager clipboard = (ClipboardManager) getTargetContext().getSystemService(CLIPBOARD_SERVICE);
    int lastItemIndex = clipboard.getPrimaryClip().getItemCount() - 1;
    return clipboard.getPrimaryClip().getItemAt(lastItemIndex).getText().toString();
  }

  private void saveHttpCall(String url, String method,
                            int statusCode, String statusText, String responseBody, String requestPayload) {
    Map<String, List<String>> requestHeaders = ImmutableMap.of(
      "content-type", singletonList("application/json"),
      "content-length", singletonList("403"),
      "accept-language", asList("en-US,en", "q=0.8,hi", "q=0.6"),
      ":scheme", singletonList("https")
    );
    Map<String, List<String>> responseHeaders = ImmutableMap.of(
      "content-type", singletonList("application/json"),
      "cache-control", singletonList("no-cache"),
      "content-disposition", singletonList("attachment"),
      "date", singletonList("Sun, 02 Apr 2017 08:54:39 GMT")
    );
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withStatusCode(statusCode)
      .withStatusText(statusText)
      .withResponseBody(responseBody)
      .withPayload(requestPayload)
      .withRequestHeaders(requestHeaders)
      .withResponseHeaders(responseHeaders)
      .build();
    snooperRepo.save(httpCall);
  }
}
