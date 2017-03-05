package com.prateekj.snooper.repo;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.model.IdInitializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static io.realm.Sort.DESCENDING;

public class SnooperRepo {
  private Realm realm;

  public SnooperRepo(Realm realm) {
    this.realm = realm;
  }

  public void save(HttpCall httpCall) {
    httpCall.setDate(new Date());
    new IdInitializer(this.realm).initialize(httpCall);
    realm.beginTransaction();
    realm.copyToRealm(httpCall);
    realm.commitTransaction();
  }

  public List<HttpCall> findAll() {
    ArrayList<HttpCall> httpCalls = new ArrayList<>();
    for (HttpCall httpCall : realm.where(HttpCall.class).findAllSorted("date", DESCENDING)) {
      httpCalls.add(httpCall);
    }
    return httpCalls;
  }

  public HttpCall findById(int id) {
    return realm.where(HttpCall.class).equalTo("id", id).findFirst();
  }
}
