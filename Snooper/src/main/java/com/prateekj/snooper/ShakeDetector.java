package com.prateekj.snooper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

  private OnShakeListener onShakeListener;
  private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
  private static final int SHAKE_SLOP_TIME_MS = 500;
  private long mShakeTimestamp;


  public ShakeDetector(OnShakeListener shakeListener) {
    this.onShakeListener = shakeListener;
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    float gX = x / SensorManager.GRAVITY_EARTH;
    float gY = y / SensorManager.GRAVITY_EARTH;
    float gZ = z / SensorManager.GRAVITY_EARTH;

    // gForce will be close to 1 when there is no movement.
    float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
      final long now = System.currentTimeMillis();
      // ignore shake events too close to each other (500ms)
      if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
        return;
      }

      mShakeTimestamp = now;
      this.onShakeListener.onShake();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }
}
