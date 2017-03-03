package com.prateekj.snooper;

import android.content.Context;
import android.content.Intent;

import com.prateekj.snooper.activity.HttpCallListActivity;

public class SnooperShakeListener implements OnShakeListener{

  private Context context;

  public SnooperShakeListener(Context context) {
    this.context = context;
  }

  @Override
  public void onShake() {
    Intent intent = new Intent(this.context, HttpCallListActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    this.context.startActivity(intent);
  }
}
