package com.prateekj.snooper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.views.DividerItemDecoration;

public class HttpCallListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    RecyclerView httpCallList = (RecyclerView) findViewById(R.id.list);
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    HttpCallListAdapter httpCallListAdapter = new HttpCallListAdapter(repo);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    httpCallList.addItemDecoration(itemDecoration);
    httpCallList.setAdapter(httpCallListAdapter);
  }
}
