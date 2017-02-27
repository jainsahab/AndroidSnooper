package com.prateekj.snooper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.presenter.HttpCallListPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.views.HttpListView;

import static com.prateekj.snooper.activity.ResponseBodyActivity.HTTP_CALL_ID;

public class HttpCallListActivity extends AppCompatActivity implements HttpListView {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    RecyclerView httpCallList = (RecyclerView) findViewById(R.id.list);
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    HttpCallListPresenter presenter = new HttpCallListPresenter(this);
    HttpCallListAdapter httpCallListAdapter = new HttpCallListAdapter(repo, presenter);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    httpCallList.addItemDecoration(itemDecoration);
    httpCallList.setAdapter(httpCallListAdapter);
  }

  @Override
  public void navigateToResponseBody(int httpCallId) {
    Intent intent = new Intent(this, ResponseBodyActivity.class);
    intent.putExtra(HTTP_CALL_ID, httpCallId);
    startActivity(intent);
  }
}
