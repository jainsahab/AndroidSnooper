package com.prateekj.snooper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.prateekj.snooper.infra.BackgroundManager;
import com.prateekj.snooper.infra.CurrentActivityManager;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;

import java.io.IOException;

import static com.prateekj.snooper.SnooperFlowSelectionDialog.DEFAULT_FRAGMENT_TAG;

public class AndroidSnooper implements BackgroundManager.Listener, SnooperShakeAction, CurrentActivityManager.Listener {

  public static final String ACTION_END_SNOOPER_FLOW = "com.snooper.END_SNOOPER_FLOW";
  private Context context;
  private static AndroidSnooper androidSnooper;
  private ShakeDetector shakeDetector;
  private Activity currentActivity;
  private SnooperRepo snooperRepo;
  private SnooperFlowSelectionDialog snooperFlowSelectionDialog;

  private AndroidSnooper() {}

  public void record(final HttpCall httpCall) throws IOException {
    Handler handler = new Handler(context.getMainLooper());
    handler.post(new Runnable() {
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
    snooperFlowSelectionDialog = new SnooperFlowSelectionDialog();
    snooperFlowSelectionDialog.show(this.currentActivity.getFragmentManager(), DEFAULT_FRAGMENT_TAG);
  }

  @Override
  public void endSnooperFlow() {
    if (isDialogVisible()) {
      snooperFlowSelectionDialog.dismiss();
    }
    Intent intent = new Intent(ACTION_END_SNOOPER_FLOW);
    LocalBroadcastManager.getInstance(AndroidSnooper.this.context).sendBroadcast(intent);
  }

  private boolean isDialogVisible() {
    return snooperFlowSelectionDialog != null &&
      snooperFlowSelectionDialog.getDialog() != null &&
      snooperFlowSelectionDialog.getDialog().isShowing();
  }

  public static AndroidSnooper init(Application application) {
    if (androidSnooper != null) {
      return androidSnooper;
    }
    androidSnooper = new AndroidSnooper();
    androidSnooper.context = application;
    androidSnooper.snooperRepo = new SnooperRepo(androidSnooper.context);
    androidSnooper.shakeDetector = new ShakeDetector(new SnooperShakeListener(androidSnooper));
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
