package com.prateekj.snooper.infra

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermissionChecker(private val activity: Activity) {
  private var permissionRequestCode: Int = 0
  private var callBack: PermissionRequestCallBack? = null

  fun handlePermission(
    permission: String,
    permissionRequestCode: Int,
    callBack: PermissionRequestCallBack
  ) {
    this.permissionRequestCode = permissionRequestCode
    this.callBack = callBack
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(
          activity,
          permission
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        ActivityCompat.requestPermissions(
          activity,
          arrayOf(permission),
          permissionRequestCode
        )
      } else {
        callBack.permissionGranted()
      }
    } else {
      callBack.permissionGranted()
    }
  }

  fun handlePermissionResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (requestCode == permissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      callBack!!.permissionGranted()
    } else {
      callBack!!.permissionDenied()
    }

  }

  interface PermissionRequestCallBack {
    fun permissionGranted()
    fun permissionDenied()
  }
}