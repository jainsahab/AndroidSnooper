package com.prateekj.snooper.networksnooper.repo;

import android.support.annotation.NonNull;

import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.prateekj.snooper.utils.TestUtilities.getCalendar;
import static com.prateekj.snooper.utils.TestUtilities.getDate;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.YEAR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SnooperRepoTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  private Realm realm;
  private SnooperRepo snooperRepo;

  @Before
  public void setUp() throws Exception {
    realm = rule.getRealm();
    snooperRepo = new SnooperRepo(realm);
  }

  @Test
  public void shouldSaveHttpCallWithTodayDate() throws Exception {
    HttpCall httpCall = new HttpCall.Builder().withUrl("url1").build();
    snooperRepo.save(httpCall);
    Calendar todayDate = Calendar.getInstance();

    HttpCall call = realm.where(HttpCall.class).findFirst();

    Calendar actualCalendar = Calendar.getInstance();
    actualCalendar.setTime(call.getDate());
    assertThat(call.getUrl(), is("url1"));
    assertThat(actualCalendar.get(DATE), is(todayDate.get(DATE)));
    assertThat(actualCalendar.get(DAY_OF_MONTH), is(todayDate.get(DAY_OF_MONTH)));
    assertThat(actualCalendar.get(YEAR), is(todayDate.get(YEAR)));
  }

  @Test
  public void shouldGetAllHttpCallsInTheDateDescendingOrder() throws Exception {
    Date beforeDate = getDate(2016, 5, 23);
    Date afterDate = getDate(2016, 5, 24);
    HttpCall httpCall1 = new HttpCall.Builder().withUrl("url1").build();
    HttpCall httpCall2 = new HttpCall.Builder().withUrl("url2").build();
    snooperRepo.save(httpCall1);
    snooperRepo.save(httpCall2);
    updateHttpCallWithId(1, afterDate);
    updateHttpCallWithId(2, beforeDate);

    List<HttpCall> httpCalls = snooperRepo.findAll();

    assertThat(httpCalls, hasCallWithUrl("url1"));
    assertThat(httpCalls, hasCallWithUrl("url2"));
    assertThat(httpCalls.get(0), hasDate(getCalendar(afterDate)));
    assertThat(httpCalls.get(1), hasDate(getCalendar(beforeDate)));
  }

  @Test
  public void shouldReturnHttpCallsByGivenId() throws Exception {
    HttpCall httpCall1 = new HttpCall.Builder().withUrl("url1").build();
    HttpCall httpCall2 = new HttpCall.Builder().withUrl("url2").build();

    SnooperRepo snooperRepo = new SnooperRepo(realm);
    snooperRepo.save(httpCall1);
    snooperRepo.save(httpCall2);

    HttpCall firstPersistedHttpCall = snooperRepo.findById(1);
    HttpCall secondPersistedHttpCall = snooperRepo.findById(2);

    assertThat(firstPersistedHttpCall.getUrl(), is("url1"));
    assertThat(secondPersistedHttpCall.getUrl(), is("url2"));
  }

  @Test
  public void shouldDeleteAllTheRecords() throws Exception {
    HttpCall httpCall = new HttpCall.Builder().withUrl("url1").build();
    HttpCall httpCall2 = new HttpCall.Builder().withUrl("url1").build();
    snooperRepo.save(httpCall);
    snooperRepo.save(httpCall2);

    snooperRepo.deleteAll();

    List<HttpCall> httpCalls = snooperRepo.findAll();
    assertEquals(httpCalls.size(), 0);
  }

  private void updateHttpCallWithId(int id, Date date) {
    HttpCall call = snooperRepo.findById(id);
    realm.beginTransaction();
    call.setDate(date);
    realm.copyToRealmOrUpdate(call);
    realm.commitTransaction();
  }

  private Matcher<? super HttpCall> hasDate(final Calendar date) {
    return new CustomTypeSafeMatcher<HttpCall>("has date: " + date) {
      @Override
      protected boolean matchesSafely(HttpCall item) {
        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(item.getDate());
        assertThat(actualCalendar.get(DATE), is(date.get(DATE)));
        assertThat(actualCalendar.get(DAY_OF_MONTH), is(date.get(DAY_OF_MONTH)));
        assertThat(actualCalendar.get(YEAR), is(date.get(YEAR)));
        return true;
      }
    };
  }

  @NonNull
  private CustomTypeSafeMatcher<List<HttpCall>> hasCallWithUrl(final String url) {
    return new CustomTypeSafeMatcher<List<HttpCall>>("with url") {
      @Override
      protected boolean matchesSafely(List<HttpCall> item) {
        for (HttpCall httpCall : item) {
          if (httpCall.getUrl().equals(url)) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
