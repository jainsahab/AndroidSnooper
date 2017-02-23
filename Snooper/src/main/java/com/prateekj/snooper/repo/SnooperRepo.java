package com.prateekj.snooper.repo;

import com.prateekj.snooper.HttpCall;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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

  public List<HttpCall> findAll() {
    RealmResults<HttpCall> results = realm.where(HttpCall.class).findAll();
    ArrayList<HttpCall> httpCalls = new ArrayList<>();
    for (int index = 0 ; index < results.size(); index++) {
      httpCalls.add(results.get(index));
    }
    return httpCalls;
  }
}
