package com.prateekj.snooper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.prateekj.snooper.activity.HttpCallListActivity;
import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.infra.BackgroundManager;

import java.io.IOException;

public class AndroidSnooper implements BackgroundManager.Listener, SnooperShakeAction {

  private static final String TAG = AndroidSnooper.class.getSimpleName();
  public static final String END_SNOOPER_FLOW = "END_SNOOPER_FLOW";
  private static Context context;
  private static AndroidSnooper androidSnooper;
  private SnooperRepo snooperRepo;
  private ShakeDetector shakeDetector;

  private AndroidSnooper() {}

  public void record(final HttpCall httpCall) throws IOException {
    Handler handler = new Handler(context.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        AndroidSnooper.this.snooperRepo.save(httpCall);
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
  public void startSnooperFlow() {
    Intent intent = new Intent(AndroidSnooper.context, HttpCallListActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    AndroidSnooper.context.startActivity(intent);
  }

  @Override
  public void endSnooperFlow() {
    Intent intent = new Intent(END_SNOOPER_FLOW);
    LocalBroadcastManager.getInstance(AndroidSnooper.context).sendBroadcast(intent);
  }

  public static AndroidSnooper init(Application application) {
    if (androidSnooper != null) {
      return androidSnooper;
    }
    AndroidSnooper.context = application;
    androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = new SnooperRepo(RealmFactory.create(context));
    androidSnooper.shakeDetector = new ShakeDetector(new SnooperShakeListener(androidSnooper));
    BackgroundManager.getInstance(application).registerListener(androidSnooper);
    return androidSnooper;
  }

  public static AndroidSnooper getInstance() {
    if (androidSnooper == null) {
      throw new RuntimeException("Android Snooper is not initialized yet");
    }
    return androidSnooper;
  }

  private void registerSensorListener() {
    Log.d(TAG, "Registering Listener");
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  private void unRegisterSensorListener() {
    Log.d(TAG, "Unregistering Listener");
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    sManager.unregisterListener(shakeDetector);
  }
}
