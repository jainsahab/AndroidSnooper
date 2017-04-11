package com.prateekj.snooper;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.infra.BackgroundManager;

import java.io.IOException;

public class AndroidSnooper {

  private static Context context;
  private SnooperRepo snooperRepo;
  private static AndroidSnooper androidSnooper;

  private AndroidSnooper() {
  }

  public void record(final HttpCall httpCall) throws IOException {
    Handler handler = new Handler(context.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        AndroidSnooper.this.snooperRepo.save(httpCall);
      }
    });
  }

  public static AndroidSnooper init(Application application) {
    AndroidSnooper.context = application;
    if (androidSnooper != null) {
      return androidSnooper;
    }
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(context));
    ShakeDetector shakeDetector = new ShakeDetector(new SnooperShakeListener(context));
    BackgroundManager backgroundManager = BackgroundManager.getInstance(application);
    backgroundManager.registerListener(shakeDetector);
    androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = repo;
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    return androidSnooper;
  }

  public static AndroidSnooper getInstance() {
    if (androidSnooper == null) {
      throw new RuntimeException("Android Snooper is not initialized yet");
    }
    return androidSnooper;
  }
}
