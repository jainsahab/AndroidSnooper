package com.prateekj.snooper.repo;

import com.prateekj.snooper.HttpCall;

import io.realm.Realm;

public class SnooperRepo {
  private Realm realm;

  public SnooperRepo(Realm realm) {
    this.realm = realm;
  }

  public void save(HttpCall httpCall) {
    realm.beginTransaction();
    realm.copyToRealm(httpCall);
    realm.commitTransaction();
  }
}
