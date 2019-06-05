package com.prateekj.snooper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.prateekj.snooper.infra.BackgroundManager;
import com.prateekj.snooper.infra.CurrentActivityManager;
import com.prateekj.snooper.networksnooper.activity.HttpCallListActivity;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;

import java.io.IOException;

public class AndroidSnooper implements BackgroundManager.Listener, SnooperShakeAction, CurrentActivityManager.Listener {

  public static final String ACTION_END_SNOOPER_FLOW = "com.snooper.END_SNOOPER_FLOW";
  private Context context;
  private static AndroidSnooper androidSnooper;
  private ShakeDetector shakeDetector;
  private Activity currentActivity;
  private SnooperRepo snooperRepo;
  private HandlerThread writeThread;
  private Handler writeHandler;

  private AndroidSnooper() {}

  public void record(final HttpCall httpCall) throws IOException {
    writeHandler.post(new Runnable() {
      @Override
      public void run() {
        AndroidSnooper.this.snooperRepo.save(HttpCallRecord.from(httpCall));
      }
    });
  }

  @Override
  public void onBecameForeground() {
    registerSensorListener();
  }

  @Override
  public void onBecameBackground() {
    unRegisterSensorListener();
  }

  @Override
  public void currentActivity(Activity activity) {
    this.currentActivity = activity;
  }

  @Override
  public void startSnooperFlow() {
    Intent intent = new Intent(AndroidSnooper.this.context, HttpCallListActivity.class);
    this.currentActivity.startActivity(intent);
  }

  @Override
  public void endSnooperFlow() {
    Intent intent = new Intent(ACTION_END_SNOOPER_FLOW);
    LocalBroadcastManager.getInstance(AndroidSnooper.this.context).sendBroadcast(intent);
  }

  public static AndroidSnooper init(Application application) {
    if (androidSnooper != null) {
      return androidSnooper;
    }
    androidSnooper = new AndroidSnooper();
    androidSnooper.context = application;
    androidSnooper.snooperRepo = new SnooperRepo(androidSnooper.context);
    androidSnooper.shakeDetector = new ShakeDetector(new SnooperShakeListener(androidSnooper));
    androidSnooper.writeThread = new HandlerThread("AndroidSnooper:Writer");
    androidSnooper.writeThread.start();
    androidSnooper.writeHandler = new Handler(androidSnooper.writeThread.getLooper());

    BackgroundManager.getInstance(application).registerListener(androidSnooper);
    CurrentActivityManager.getInstance(application).registerListener(androidSnooper);
    return androidSnooper;
  }

  public static AndroidSnooper getInstance() {
    if (androidSnooper == null) {
      throw new RuntimeException("Android Snooper is not initialized yet");
    }
    return androidSnooper;
  }

  private void registerSensorListener() {
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  private void unRegisterSensorListener() {
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    sManager.unregisterListener(shakeDetector);
  }
}
