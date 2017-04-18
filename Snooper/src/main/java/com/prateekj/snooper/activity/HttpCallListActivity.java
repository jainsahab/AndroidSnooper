package com.prateekj.snooper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.prateekj.snooper.R;
import com.prateekj.snooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.presenter.HttpCallListPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.views.HttpListView;

import static com.prateekj.snooper.AndroidSnooper.END_SNOOPER_FLOW;
import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class HttpCallListActivity extends AppCompatActivity implements HttpListView {

  private HttpCallListPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    RecyclerView httpCallList = (RecyclerView) findViewById(R.id.list);
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    presenter = new HttpCallListPresenter(this);
    HttpCallListAdapter httpCallListAdapter = new HttpCallListAdapter(repo, presenter);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    httpCallList.addItemDecoration(itemDecoration);
    httpCallList.setItemAnimator(new DefaultItemAnimator());
    httpCallList.setAdapter(httpCallListAdapter);
    LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver(), new IntentFilter(END_SNOOPER_FLOW));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.http_call_list_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.done_menu) {
      presenter.onDoneClick();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void navigateToResponseBody(int httpCallId) {
    Intent intent = new Intent(this, HttpCallActivity.class);
    intent.putExtra(HTTP_CALL_ID, httpCallId);
    startActivity(intent);
    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
  }

  @Override
  public void finishView() {
    finish();
  }

  private BroadcastReceiver finishActivityReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        HttpCallListActivity.this.finish();
      }
    };
  }
}
