package com.prateekj.snooper.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.prateekj.snooper.R;
import com.prateekj.snooper.fragment.ResponseBodyFragment;

public class HttpCallActivity extends AppCompatActivity {

  public static String HTTP_CALL_ID = "HTTP_CALL_ID";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_response_body);
    ResponseBodyFragment fragment = new ResponseBodyFragment();
    fragment.setArguments(getIntent().getExtras());
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.container, fragment);
    fragmentTransaction.commit();
  }
}
