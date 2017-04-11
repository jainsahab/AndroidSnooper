package com.prateekj.snooper;

import android.app.Application;
import android.support.test.espresso.core.deps.guava.base.Predicate;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.HttpCall.Builder;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.model.HttpHeaderValue;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.core.deps.guava.collect.Iterables.any;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class AndroidSnooperTest {

  private AndroidSnooper androidSnooper;

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Before
  public void setUp() throws Exception {
    androidSnooper = AndroidSnooper.getInstance();
  }

  @Test
  public void shouldReturnSameInstanceOnEveryInit() throws Exception {
    Application application = ((SnooperInstrumentationRunner) getInstrumentation()).getApplication();
    AndroidSnooper newSnooper = AndroidSnooper.init(application);
    assertThat(newSnooper, sameInstance(androidSnooper));
  }

  @Test
  public void shouldSaveHttpCallViaSpringHttpRequestInterceptor() throws Exception {
    final String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0";
    final String responseBody = "responseBody";
    final String requestBody = "requestBody";

    HttpCall call = new Builder()
      .withUrl(url)
      .withMethod("POST")
      .withPayload(requestBody)
      .withResponseBody(responseBody)
      .withStatusCode(200)
      .withStatusText("OK")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build();

    androidSnooper.record(call);
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        Realm realm = TestApplication.getInstance().getRealm();
        HttpCall httpCall = realm.where(HttpCall.class).findAll().first();
        assertThat(httpCall.getUrl(), is(url));
        assertThat(httpCall.getPayload(), is(requestBody));
        assertThat(httpCall.getMethod(), is("POST"));
        assertThat(httpCall.getResponseBody(), is(responseBody));
        assertThat(httpCall.getStatusCode(), is(200));
        assertThat(httpCall.getStatusText(), is("OK"));

        HttpHeader userAgentHeader = httpCall.getRequestHeader("User-Agent");
        assertThat(userAgentHeader.getValues(), containsWithValue("Android Browser"));
        HttpHeader cacheControlHeader = httpCall.getRequestHeader("cache-control");
        assertThat(cacheControlHeader.getValues(), containsWithValue("public"));
        assertThat(cacheControlHeader.getValues(), containsWithValue("max-age=86400"));
        assertThat(cacheControlHeader.getValues(), containsWithValue("no-transform"));

        HttpHeader dateHeader = httpCall.getResponseHeader("date");
        assertThat(dateHeader.getValues(), containsWithValue("Thu, 02 Mar 2017 13:03:11 GMT"));
        HttpHeader xssProtectionHeader = httpCall.getResponseHeader("x-xss-protection");
        assertThat(xssProtectionHeader.getValues(), containsWithValue("1"));
        assertThat(xssProtectionHeader.getValues(), containsWithValue("mode=block"));
      }
    });
  }

  private Matcher<? super RealmList<HttpHeaderValue>> containsWithValue(final String value) {
    return new CustomTypeSafeMatcher<RealmList<HttpHeaderValue>>("contains with value:" + value) {
      @Override
      protected boolean matchesSafely(RealmList<HttpHeaderValue> list) {
        return any(list, new Predicate<HttpHeaderValue>() {
          @Override
          public boolean apply(@Nullable HttpHeaderValue httpHeaderValue) {
            return httpHeaderValue.getValue().equals(value);
          }
        });
      }
    };
  }

  private Map<String, List<String>> getResponseHeaders() {
    Map<String, List<String>> headers = new HashMap<>();
    List<String> xssProtectionHeader = Arrays.asList("1", "mode=block");
    List<String> dateHeader = singletonList("Thu, 02 Mar 2017 13:03:11 GMT");
    headers.put("x-xss-protection", xssProtectionHeader);
    headers.put("date", dateHeader);
    return headers;
  }

  private Map<String, List<String>> getRequestHeaders() {
    Map<String, List<String>> headers = new HashMap<>();
    List<String> cacheControlHeader = Arrays.asList("public", "max-age=86400", "no-transform");
    List<String> userAgentHeader = singletonList("Android Browser");
    headers.put("User-Agent", userAgentHeader);
    headers.put("cache-control", cacheControlHeader);
    return headers;
  }
}
