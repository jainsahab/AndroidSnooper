package com.prateekj.snooper.networksnooper.activity;

import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.google.common.collect.ImmutableMap;
import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel;
import com.prateekj.snooper.rules.RealmCleanRule;
import com.prateekj.snooper.rules.RunUsingLooper;
import com.prateekj.snooper.utils.EspressoIntentMatchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Intent.ACTION_CHOOSER;
import static android.content.Intent.ACTION_SEND;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
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
  public IntentsTestRule<HttpCallActivity> activityRule =
    new IntentsTestRule<>(HttpCallActivity.class, true, false);

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

    onView(withText(R.string.title_http_call_activity)).check(matches(isDisplayed()));
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

    onView(withText("https://www.abc.com/person/1")).check(matches(isDisplayed()));
    onView(withText("GET")).check(matches(isDisplayed()));
    onView(withText("200")).check(matches(isDisplayed()));
    onView(withText("OK")).check(matches(isDisplayed()));
    onView(withText("06/02/2017 11:22:33")).check(matches(isDisplayed()));

    onView(withText(R.string.response_headers)).perform(click());
    verifyResponseHeader("content-type", "application/json");
    verifyResponseHeader("cache-control", "no-cache");
    verifyResponseHeader("content-disposition", "attachment");
    verifyResponseHeader("date", "Sun, 02 Apr 2017 08:54:39 GMT");

    onView(withText(R.string.request_headers)).perform(click());
    verifyRequestHeader("content-type", "application/json");
    verifyRequestHeader("content-length", "403");
    verifyRequestHeader("accept-language", "en-US,en;q=0.8,hi;q=0.6");
    verifyRequestHeader(":scheme", "https");

    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));
    onView(withId(R.id.share_menu)).perform(click());
    verifyClickActionOnShareMenu();

  }

  private void verifyResponseHeader(String headerName, String headerValue) {
    onData(withHeaderData(headerName, headerValue)).
      inAdapterView(withId(R.id.response_header_list)).
      check(matches(allOf(hasDescendant(withText(headerName)), hasDescendant(withText(headerValue)))));
  }

  private void verifyRequestHeader(String headerName, String headerValue) {
    onData(withHeaderData(headerName, headerValue)).
      inAdapterView(withId(R.id.request_header_list)).
      check(matches(allOf(hasDescendant(withText(headerName)), hasDescendant(withText(headerValue)))));
  }

  private Matcher<HttpHeaderViewModel> withHeaderData(final String headerName, final String headerValue) {
    return new CustomTypeSafeMatcher<HttpHeaderViewModel>("Header with") {
      @Override
      protected boolean matchesSafely(HttpHeaderViewModel viewModel) {
        return viewModel.headerName().equals(headerName) &&
          viewModel.headerValues().equals(headerValue);
      }
    };
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
    httpCall.setDate(getDate(2017, 5, 2, 11, 22, 33));
    snooperRepo.save(httpCall);
  }

  private void verifyClickActionOnShareMenu() {
    intended(allOf(
      hasExtras(EspressoIntentMatchers.forMailChooserIntent(ACTION_SEND, "*/*", "Log details", "2017_06_02_11_22_33.txt")),
      hasAction(ACTION_CHOOSER)));
  }
}
