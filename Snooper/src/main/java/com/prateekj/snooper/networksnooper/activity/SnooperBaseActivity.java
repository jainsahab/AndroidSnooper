package com.prateekj.snooper.networksnooper.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.prateekj.snooper.infra.AppPermissionChecker;

import static com.prateekj.snooper.AndroidSnooper.ACTION_END_SNOOPER_FLOW;

public abstract class SnooperBaseActivity extends AppCompatActivity {

  private BroadcastReceiver receiver;
  protected AppPermissionChecker appPermissionChecker;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    receiver = finishActivityReceiver(this);
    appPermissionChecker = new AppPermissionChecker(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ACTION_END_SNOOPER_FLOW));
  }

  @Override
  protected void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    appPermissionChecker.handlePermissionResult(requestCode, permissions, grantResults);
  }

  private BroadcastReceiver finishActivityReceiver(final Activity activity) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        ActivityCompat.finishAffinity(activity);
      }
    };
  }

}
