package com.prateekj.snooper.rules;


import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;
import com.prateekj.snooper.realm.RealmFactory;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.realm.Realm;

import static android.support.test.InstrumentationRegistry.getTargetContext;

public class RealmCleanRule implements TestRule {

  private final Realm realm;

  public RealmCleanRule() {
    realm = RealmFactory.create(getTargetContext());
  }

  public Realm getRealm() {
    return realm;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        } finally {
          realm.beginTransaction();
          realm.delete(HttpCall.class);
          realm.delete(HttpHeader.class);
          realm.delete(HttpHeaderValue.class);
          realm.commitTransaction();
        }
      }
    };
  }
}
