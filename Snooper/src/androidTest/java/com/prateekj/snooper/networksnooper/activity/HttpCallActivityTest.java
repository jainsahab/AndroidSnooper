package com.prateekj.snooper.networksnooper.activity;

import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.common.collect.ImmutableMap;
import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel;
import com.prateekj.snooper.rules.DataResetRule;
import com.prateekj.snooper.rules.RunUsingLooper;
import com.prateekj.snooper.utils.EspressoIntentMatchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Intent.ACTION_CHOOSER;
import static android.content.Intent.ACTION_SEND;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.utils.EspressoViewMatchers.hasBackgroundSpanOn;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

public class HttpCallActivityTest {

  @Rule
  public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(WRITE_EXTERNAL_STORAGE);

  @Rule
  public DataResetRule rule = new DataResetRule();

  @Rule
  public RunUsingLooper runUsingLooper = new RunUsingLooper();

  @Rule
  public IntentsTestRule<HttpCallActivity> activityRule =
    new IntentsTestRule<>(HttpCallActivity.class, true, false);

  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    snooperRepo = new SnooperRepo(getTargetContext());
  }

  @Test
  public void shouldRenderRequestAndResponseBody() throws Exception {
    String responseBody = readFrom("person_details_raw_response.json");
    String requestPayload = readFrom("person_details_raw_request.json");
    long callId = saveHttpCall("https://www.abc.com/person/1", "GET", 200, "OK", responseBody, requestPayload);

    Intent intent = new Intent();
    intent.putExtra(HTTP_CALL_ID, callId);

    activityRule.launchActivity(intent);

    onView(withText(R.string.title_http_call_activity)).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_response.json"))));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(readFrom("person_details_formatted_response.json")));
    onView(withId(R.id.search_menu)).perform(click());
    onView(withId(R.id.search_src_text)).perform(typeText("streetaddress"));
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(hasBackgroundSpanOn("streetAddress", R.color.snooper_text_highlight_color)));

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click());
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(readFrom("person_details_formatted_request.json")));
    onView(withId(R.id.search_menu)).perform(click());
    onView(withId(R.id.search_src_text)).perform(typeText("delhi"));
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(hasBackgroundSpanOn("Delhi", R.color.snooper_text_highlight_color)));

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

  @Test
  public void shouldRenderRequestAndErrorBody() throws Exception {
    String requestPayload = readFrom("person_details_raw_request.json");
    String error = "java.net.ConnectException: failed to connect to localhost/127.0.0.1 " +
      "(port 80) after 30000ms: isConnected failed: ECONNREFUSED (Connection refused)";
    long callId = saveHttpCallWithError("https://www.abc.com/person/1", "GET", error, requestPayload);

    Intent intent = new Intent();
    intent.putExtra(HTTP_CALL_ID, callId);

    activityRule.launchActivity(intent);

    onView(withText("ERROR")).check(matches(isDisplayed()));
    onView(withText(error)).check(matches(isDisplayed()));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(error));

    onView(withText("REQUEST")).check(matches(isDisplayed())).perform(click());
    onView(allOf(withId(R.id.payload_text), isDisplayed()))
      .check(matches(withText(readFrom("person_details_formatted_request.json"))));
    onView(withId(R.id.copy_menu)).perform(click());
    assertThat(clipBoardText(), is(readFrom("person_details_formatted_request.json")));

    onView(withText("HEADERS")).check(matches(isDisplayed())).perform(click());

    onView(withText("https://www.abc.com/person/1")).check(matches(isDisplayed()));
    onView(withText("FAILED")).check(matches(isDisplayed()));
    onView(withText("06/02/2017 11:22:33")).check(matches(isDisplayed()));

    onView(withText(R.string.request_headers)).perform(click());
    verifyRequestHeader("content-type", "application/json");
    verifyRequestHeader("content-length", "403");
    verifyRequestHeader("accept-language", "en-US,en;q=0.8,hi;q=0.6");
    verifyRequestHeader(":scheme", "https");
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

  private long saveHttpCall(String url, String method, int statusCode,
                            String statusText, String responseBody, String requestPayload) {
    Map<String, List<String>> requestHeaders = getRequestHeaders();
    Map<String, List<String>> responseHeaders = getResponseHeaders();
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
    return snooperRepo.save(HttpCallRecord.from(httpCall));
  }

  private long saveHttpCallWithError(String url, String method, String error, String requestPayload) {
    Map<String, List<String>> requestHeaders = getRequestHeaders();
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(url)
      .withMethod(method)
      .withError(error)
      .withPayload(requestPayload)
      .withRequestHeaders(requestHeaders)
      .build();
    httpCall.setDate(getDate(2017, 5, 2, 11, 22, 33));
    return snooperRepo.save(HttpCallRecord.from(httpCall));
  }

  @NonNull
  private Map<String, List<String>> getResponseHeaders() {
    return ImmutableMap.of(
      "content-type", singletonList("application/json"),
      "cache-control", singletonList("no-cache"),
      "content-disposition", singletonList("attachment"),
      "date", singletonList("Sun, 02 Apr 2017 08:54:39 GMT")
    );
  }

  @NonNull
  private Map<String, List<String>> getRequestHeaders() {
    return ImmutableMap.of(
      "content-type", singletonList("application/json"),
      "content-length", singletonList("403"),
      "accept-language", asList("en-US,en", "q=0.8,hi", "q=0.6"),
      ":scheme", singletonList("https")
    );
  }

  private void verifyClickActionOnShareMenu() {
    intended(allOf(
      hasExtras(EspressoIntentMatchers.forMailChooserIntent(ACTION_SEND, "*/*", "Log details", "2017_06_02_11_22_33.txt")),
      hasAction(ACTION_CHOOSER)));
  }
}
