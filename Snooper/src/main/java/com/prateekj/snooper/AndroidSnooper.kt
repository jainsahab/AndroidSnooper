package com.prateekj.snooper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.prateekj.snooper.infra.BackgroundManager
import com.prateekj.snooper.infra.CurrentActivityManager
import com.prateekj.snooper.networksnooper.activity.HttpCallListActivity
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord

import java.io.IOException

class AndroidSnooper private constructor() : BackgroundManager.Listener, SnooperShakeAction,
  CurrentActivityManager.Listener {
  private lateinit var context: Context
  private lateinit var shakeDetector: ShakeDetector
  private var currentActivity: Activity? = null
  private lateinit var snooperRepo: SnooperRepo
  private lateinit var writeThread: HandlerThread
  private lateinit var writeHandler: Handler

  @Throws(IOException::class)
  fun record(httpCall: HttpCall) {
    writeHandler.post { this@AndroidSnooper.snooperRepo.save(HttpCallRecord.from(httpCall)) }
  }

  override fun onBecameForeground() {
    registerSensorListener()
  }

  override fun onBecameBackground() {
    unRegisterSensorListener()
  }

  override fun currentActivity(activity: Activity?) {
    this.currentActivity = activity
  }

  override fun startSnooperFlow() {
    val intent = Intent(this@AndroidSnooper.context, HttpCallListActivity::class.java)
    this.currentActivity?.startActivity(intent)
  }

  override fun endSnooperFlow() {
    val intent = Intent(ACTION_END_SNOOPER_FLOW)
    LocalBroadcastManager.getInstance(this@AndroidSnooper.context).sendBroadcast(intent)
  }

  private fun registerSensorListener() {
    val sManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  private fun unRegisterSensorListener() {
    val sManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sManager.unregisterListener(shakeDetector)
  }

  companion object {
    const val ACTION_END_SNOOPER_FLOW = "com.snooper.END_SNOOPER_FLOW"

    @Volatile
    private var INSTANCE: AndroidSnooper? = null

    @JvmStatic
    fun init(application: Application): AndroidSnooper {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildAndroidSnooper(application).also { INSTANCE = it }
      }
    }

    private fun buildAndroidSnooper(application: Application): AndroidSnooper {
      val androidSnooper = AndroidSnooper()
      androidSnooper.context = application
      androidSnooper.snooperRepo = SnooperRepo(androidSnooper.context)
      androidSnooper.shakeDetector = ShakeDetector(SnooperShakeListener(androidSnooper))
      androidSnooper.writeThread = HandlerThread("AndroidSnooper:Writer")
      androidSnooper.writeThread.start()
      androidSnooper.writeHandler = Handler(androidSnooper.writeThread.looper)

      BackgroundManager.getInstance(application).registerListener(androidSnooper)
      CurrentActivityManager.getInstance(application).registerListener(androidSnooper)

      return androidSnooper
    }

    @JvmStatic
    val instance: AndroidSnooper
      get() {
        if (INSTANCE == null) {
          throw RuntimeException("Android Snooper is not initialized yet")
        }
        return INSTANCE!!
      }
  }
}
