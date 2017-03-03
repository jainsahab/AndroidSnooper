package com.prateekj.snooper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;

import java.io.IOException;

public class AndroidSnooper {

  private SnooperRepo snooperRepo;
  private static AndroidSnooper androidSnooper;

  private AndroidSnooper() {
  }

  public void record(HttpCall httpCall) throws IOException {
    this.snooperRepo.save(httpCall);
  }

  public static AndroidSnooper init(Context context) {
    if (androidSnooper != null) {
      return androidSnooper;
    }
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(context));
    androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = repo;
    SensorManager sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sManager.registerListener(new ShakeDetector(new SnooperShakeListener(context)), sensor, SensorManager.SENSOR_DELAY_NORMAL);
    return androidSnooper;
  }
}
