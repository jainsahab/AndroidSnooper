package com.prateekj.snooper.networksnooper.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem

import com.prateekj.snooper.infra.AppPermissionChecker

import com.prateekj.snooper.AndroidSnooper.Companion.ACTION_END_SNOOPER_FLOW

abstract class SnooperBaseActivity : AppCompatActivity() {

  private lateinit var receiver: BroadcastReceiver
  protected lateinit var appPermissionChecker: AppPermissionChecker

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    receiver = finishActivityReceiver(this)
    appPermissionChecker = AppPermissionChecker(this)
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(receiver, IntentFilter(ACTION_END_SNOOPER_FLOW))
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    appPermissionChecker.handlePermissionResult(requestCode, permissions, grantResults)
  }

  private fun finishActivityReceiver(activity: Activity): BroadcastReceiver {
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        ActivityCompat.finishAffinity(activity)
      }
    }
  }
}
