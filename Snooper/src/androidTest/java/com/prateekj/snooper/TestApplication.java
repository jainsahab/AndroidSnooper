package com.prateekj.snooper;

import android.app.Application;

import com.prateekj.snooper.realm.RealmFactory;

import io.realm.Realm;

public class TestApplication extends Application {
  private static TestApplication application;
  private Realm realm;

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
    AndroidSnooper.init(this);
    realm = RealmFactory.create(this);
  }

  public Realm getRealm() {
    return realm;
  }

  public static TestApplication getInstance() {
    return application;
  }
}
