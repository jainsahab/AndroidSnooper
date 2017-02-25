package com.prateekj.snooper.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmFactory {
  public static Realm create(Context context) {
    Realm.init(context);
    RealmConfiguration configuration = new RealmConfiguration.Builder()
        .name("snooper.realm")
        .modules(new SnooperRealmModule())
        .build();
    return Realm.getInstance(configuration);
  }
}
