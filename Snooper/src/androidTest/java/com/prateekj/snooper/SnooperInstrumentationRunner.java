package com.prateekj.snooper;

import android.app.Application;
import android.content.Context;
import androidx.test.runner.AndroidJUnitRunner;

public class SnooperInstrumentationRunner extends AndroidJUnitRunner {

  private Application application;

  @Override
  public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, TestApplication.class.getName(), context);
  }

  @Override
  public void callApplicationOnCreate(Application app) {
    this.application = app;
    super.callApplicationOnCreate(app);
  }

  public Application getApplication() {
    return application;
  }
}
