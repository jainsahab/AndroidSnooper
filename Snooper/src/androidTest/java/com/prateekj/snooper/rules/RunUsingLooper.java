package com.prateekj.snooper.rules;

import android.os.Looper;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RunUsingLooper implements TestRule {
  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          if(Looper.myLooper()==null) {
            Looper.prepare();
          }
          base.evaluate();
        } finally {
          Looper.myLooper().quit();
        }
      }
    };
  }
}
