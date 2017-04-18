package com.prateekj.snooper.infra;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class CurrentActivityManager implements Application.ActivityLifecycleCallbacks {

  private static CurrentActivityManager sInstance;

  public interface Listener {
    void currentActivity(Activity activity);
  }

  private final List<Listener> listeners = new ArrayList<>();

  private CurrentActivityManager(Application application) {
    application.registerActivityLifecycleCallbacks(this);
  }

  public static CurrentActivityManager getInstance(Application application) {
    if (sInstance == null) {
      sInstance = new CurrentActivityManager(application);
    }
    return sInstance;
  }


  public void registerListener(Listener listener) {
    listeners.add(listener);
  }

  public void unregisterListener(Listener listener) {
    listeners.remove(listener);
  }


  @Override
  public void onActivityResumed(Activity activity) {
    notifyListeners(activity);
  }


  @Override
  public void onActivityPaused(Activity activity) {
    notifyListeners(activity);
  }

  private void notifyListeners(Activity activity) {
    for (Listener listener : listeners) {
      listener.currentActivity(activity);
    }
  }


  @Override
  public void onActivityStopped(Activity activity) {
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
  }
}
