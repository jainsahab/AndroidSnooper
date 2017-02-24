package com.prateekj.snooper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.repo.SnooperRepo;

import io.realm.Realm;

public class HttpCallListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    Realm.init(this);
    Realm realm = Realm.getDefaultInstance();
    RecyclerView httpCallList = (RecyclerView) findViewById(R.id.list);
    SnooperRepo repo = new SnooperRepo(realm);
    HttpCallListAdapter httpCallListAdapter = new HttpCallListAdapter(repo);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    httpCallList.setAdapter(httpCallListAdapter);
  }
}
