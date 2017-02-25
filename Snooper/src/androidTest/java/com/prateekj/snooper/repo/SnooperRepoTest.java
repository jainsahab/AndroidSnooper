package com.prateekj.snooper.repo;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.rules.RealmCleanRule;

import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.realm.Realm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SnooperRepoTest {

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  private Realm realm;

  @Before
  public void setUp() throws Exception {
    Realm.init(InstrumentationRegistry.getTargetContext());
    realm = Realm.getDefaultInstance();
  }

  @Test
  public void shouldSaveHttpCall() throws Exception {
    HttpCall httpCall = new HttpCall.HttpCallBuilder().withUrl("url1").build();
    SnooperRepo snooperRepo = new SnooperRepo(realm);

    snooperRepo.save(httpCall);

    HttpCall call = realm.where(HttpCall.class).findFirst();
    assertThat(call.getUrl(), is("url1"));
  }

  @Test
  public void shouldGetAllHttpCalls() throws Exception {
    HttpCall httpCall1 = new HttpCall.HttpCallBuilder().withUrl("url1").build();
    HttpCall httpCall2 = new HttpCall.HttpCallBuilder().withUrl("url2").build();

    SnooperRepo snooperRepo = new SnooperRepo(realm);
    snooperRepo.save(httpCall1);
    snooperRepo.save(httpCall2);

    List<HttpCall> httpCalls = snooperRepo.findAll();

    assertThat(httpCalls, hasCallWithUrl("url1"));
    assertThat(httpCalls, hasCallWithUrl("url2"));
  }

  @NonNull
  private CustomTypeSafeMatcher<List<HttpCall>> hasCallWithUrl(final String url) {
    return new CustomTypeSafeMatcher<List<HttpCall>>("with url") {
      @Override
      protected boolean matchesSafely(List<HttpCall> item) {
        for (HttpCall httpCall : item) {
          if(httpCall.getUrl().equals(url)){
            return true;
          }
        }
        return  false;
      }
    };
  }
}