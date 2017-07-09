package com.prateekj.snooper.networksnooper.database;

import android.support.annotation.NonNull;
import android.support.test.espresso.core.deps.guava.base.Predicate;

import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;
import com.prateekj.snooper.rules.DataResetRule;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.core.deps.guava.collect.Iterables.any;
import static com.prateekj.snooper.utils.CollectionUtilities.last;
import static com.prateekj.snooper.utils.TestUtilities.getCalendar;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SnooperRepoTest {

  @Rule
  public DataResetRule rule = new DataResetRule();
  private SnooperRepo repo;

  @Before
  public void setUp() throws Exception {
    repo = new SnooperRepo(getTargetContext());
  }

  @Test
  public void shouldSaveAndRetrieveHttpCallRecord() throws Exception {
    HttpCall httpCall = new HttpCall.Builder().
      withUrl("http://google.com").
      withMethod("POST").
      withStatusCode(200).
      withStatusText("OK").
      withPayload("payload").
      withResponseBody("responseBody").
      withRequestHeaders(getRequestHeaders()).
      withResponseHeaders(getResponseHeaders()).
      withError("error").
      build();

    long id = repo.save(HttpCallRecord.from(httpCall));
    HttpCallRecord httpCallRecord = repo.findById(id);

    assertThat(httpCallRecord.getUrl(), is("http://google.com"));
    assertThat(httpCallRecord.getMethod(), is("POST"));
    assertThat(httpCallRecord.getStatusCode(), is(200));
    assertThat(httpCallRecord.getStatusText(), is("OK"));
    assertThat(httpCallRecord.getPayload(), is("payload"));
    assertThat(httpCallRecord.getResponseBody(), is("responseBody"));
    assertThat(httpCallRecord.getError(), is("error"));

    HttpHeader userAgentHeader = httpCallRecord.getRequestHeader("User-Agent");
    assertThat(userAgentHeader.getValues(), containsWithValue("Android Browser"));
    HttpHeader cacheControlHeader = httpCallRecord.getRequestHeader("cache-control");
    assertThat(cacheControlHeader.getValues(), containsWithValue("public"));
    assertThat(cacheControlHeader.getValues(), containsWithValue("max-age=86400"));
    assertThat(cacheControlHeader.getValues(), containsWithValue("no-transform"));

    HttpHeader dateHeader = httpCallRecord.getResponseHeader("date");
    assertThat(dateHeader.getValues(), containsWithValue("Thu, 02 Mar 2017 13:03:11 GMT"));
    HttpHeader xssProtectionHeader = httpCallRecord.getResponseHeader("x-xss-protection");
    assertThat(xssProtectionHeader.getValues(), containsWithValue("1"));
    assertThat(xssProtectionHeader.getValues(), containsWithValue("mode=block"));

  }

  @Test
  public void shouldGetAllHttpCallsInTheDateDescendingOrder() throws Exception {
    Date beforeDate = getDate(2016, 5, 23);
    Date afterDate = getDate(2016, 5, 24);
    HttpCall beforeHttpCall = new HttpCall.Builder().withUrl("url1").build();
    HttpCall afterHttpCall = new HttpCall.Builder().withUrl("url2").build();
    beforeHttpCall.setDate(beforeDate);
    afterHttpCall.setDate(afterDate);
    repo.save(HttpCallRecord.from(beforeHttpCall));
    repo.save(HttpCallRecord.from(afterHttpCall));

    List<HttpCallRecord> httpCalls = repo.findAllSortByDate();

    assertThat(httpCalls, hasCallWithUrl("url1"));
    assertThat(httpCalls, hasCallWithUrl("url2"));
    assertThat(httpCalls.get(0), hasDate(getCalendar(afterDate)));
    assertThat(httpCalls.get(1), hasDate(getCalendar(beforeDate)));
  }

  @Test
  public void shouldGetNextSetOfHttpCallsAfterTheGivenId() throws Exception {
    saveCalls(50);

    List<HttpCallRecord> httpCalls = repo.findAllSortByDateAfter(-1, 20);
    assertThat(httpCalls.size(), is(20));
    assertThat(httpCalls, areSortedAccordingToDate());

    httpCalls = repo.findAllSortByDateAfter(last(httpCalls).getId(), 20);
    assertThat(httpCalls.size(), is(20));
    assertThat(httpCalls, areSortedAccordingToDate());

    httpCalls = repo.findAllSortByDateAfter(last(httpCalls).getId(), 20);
    assertThat(httpCalls.size(), is(10));
    assertThat(httpCalls, areSortedAccordingToDate());
  }

  @NonNull
  private CustomTypeSafeMatcher<List<HttpCallRecord>> areSortedAccordingToDate() {
    return new CustomTypeSafeMatcher<List<HttpCallRecord>>("are sorted") {
      @Override
      protected boolean matchesSafely(List<HttpCallRecord> list) {
        for (int index = 0 ; index < list.size() - 1 ; index++) {
          long firstRecordTime = list.get(index).getDate().getTime();
          long secondRecordTime = list.get(index + 1).getDate().getTime();
          if (firstRecordTime < secondRecordTime) {
            return  false;
          }
        }
        return true;
      }
    };
  }

  @Test
  public void shouldDeleteAllTheRecords() throws Exception {
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl("url1")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build();
    HttpCall httpCall2 = new HttpCall.Builder()
      .withUrl("url2")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build();
    repo.save(HttpCallRecord.from(httpCall));
    repo.save(HttpCallRecord.from(httpCall2));

    repo.deleteAll();

    List<HttpCallRecord> httpCalls = repo.findAllSortByDate();
    assertThat(httpCalls.size(), is(0));
  }

  private void saveCalls(int number) {
    for (int i = 0; i < number; i++) {
      HttpCall httpCall = new HttpCall.Builder().withUrl("url" + i).build();
      Date date = new Date(getDate(2016, 5, 23).getTime() + (i * 1000));
      httpCall.setDate(date);
      repo.save(HttpCallRecord.from(httpCall));
    }
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

  private Matcher<? super List<HttpHeaderValue>> containsWithValue(final String value) {
    return new CustomTypeSafeMatcher<List<HttpHeaderValue>>("contains with value:" + value) {
      @Override
      protected boolean matchesSafely(List<HttpHeaderValue> list) {
        return any(list, new Predicate<HttpHeaderValue>() {
          @Override
          public boolean apply(@Nullable HttpHeaderValue httpHeaderValue) {
            return httpHeaderValue.getValue().equals(value);
          }
        });
      }
    };
  }

  @NonNull
  private CustomTypeSafeMatcher<List<HttpCallRecord>> hasCallWithUrl(final String url) {
    return new CustomTypeSafeMatcher<List<HttpCallRecord>>("with url") {
      @Override
      protected boolean matchesSafely(List<HttpCallRecord> item) {
        for (HttpCallRecord httpCall : item) {
          if (httpCall.getUrl().equals(url)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  private Matcher<? super HttpCallRecord> hasDate(final Calendar date) {
    return new CustomTypeSafeMatcher<HttpCallRecord>("has date: " + date) {
      @Override
      protected boolean matchesSafely(HttpCallRecord item) {
        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(item.getDate());
        assertThat(actualCalendar.get(DATE), is(date.get(DATE)));
        assertThat(actualCalendar.get(DAY_OF_MONTH), is(date.get(DAY_OF_MONTH)));
        assertThat(actualCalendar.get(YEAR), is(date.get(YEAR)));
        return true;
      }
    };
  }

}