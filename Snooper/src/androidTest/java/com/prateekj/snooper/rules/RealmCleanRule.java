package com.prateekj.snooper.rules;

import com.prateekj.snooper.HttpCall;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.realm.Realm;

import static android.support.test.InstrumentationRegistry.getTargetContext;

public class RealmCleanRule implements TestRule{

  private final Realm realm;

  public RealmCleanRule() {
    Realm.init(getTargetContext());
    realm = Realm.getDefaultInstance();
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
        realm.beginTransaction();
        realm.delete(HttpCall.class);
        realm.commitTransaction();
      }
    };
  }
}
