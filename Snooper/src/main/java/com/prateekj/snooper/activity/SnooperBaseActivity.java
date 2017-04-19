package com.prateekj.snooper.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import static com.prateekj.snooper.AndroidSnooper.ACTION_END_SNOOPER_FLOW;

public abstract class SnooperBaseActivity extends AppCompatActivity {

  private BroadcastReceiver receiver;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    receiver = finishActivityReceiver(this);
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

  private BroadcastReceiver finishActivityReceiver(final Activity activity) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        ActivityCompat.finishAffinity(activity);
      }
    };
  }

}
