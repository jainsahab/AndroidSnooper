package com.prateekj.snooper.repo;

import com.prateekj.snooper.HttpCall;

import org.junit.Test;

import io.realm.Realm;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SnooperRepoTest {
  @Test
  public void shouldSaveHttpCall() throws Exception {
    HttpCall httpCall = mock(HttpCall.class);
    Realm mockRealm = mock(Realm.class);
    SnooperRepo snooperRepo = new SnooperRepo(mockRealm);

    snooperRepo.save(httpCall);

    verify(mockRealm).beginTransaction();
    verify(mockRealm).copyToRealm(httpCall);
    verify(mockRealm).commitTransaction();
  }
}