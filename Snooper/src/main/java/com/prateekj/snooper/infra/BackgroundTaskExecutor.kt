package com.prateekj.snooper.infra

import android.app.Activity
import android.os.AsyncTask

class BackgroundTaskExecutor(private val activity: Activity) {

  fun <E> execute(backgroundTask: BackgroundTask<E>) {
    AsyncTask.execute {
      val result = backgroundTask.onExecute()
      sendResult(result, backgroundTask)
    }
  }

  fun <E> sendResult(result: E, backgroundTask: BackgroundTask<E>) {
    this.activity.runOnUiThread { backgroundTask.onResult(result) }
  }
}
