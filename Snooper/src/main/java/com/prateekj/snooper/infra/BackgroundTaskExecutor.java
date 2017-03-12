package com.prateekj.snooper.infra;

import android.app.Activity;
import android.os.AsyncTask;

public class BackgroundTaskExecutor {
  private Activity activity;

  public BackgroundTaskExecutor(Activity activity) {
    this.activity = activity;
  }

  public <E> void execute(final BackgroundTask<E> backgroundTask) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        E result = backgroundTask.onExecute();
        sendResult(result, backgroundTask);
      }
    });
  }

  public <E> void sendResult(final E result, final BackgroundTask<E> backgroundTask) {
    this.activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        backgroundTask.onResult(result);
      }
    });
  }
}
