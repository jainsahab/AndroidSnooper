package com.prateekj.snooper.networksnooper.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.networksnooper.adapter.HttpCallListClickListener;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.presenter.HttpClassSearchPresenter;
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class HttpCallSearchActivity extends SnooperBaseActivity implements HttpCallSearchView, HttpCallListClickListener, SearchView.OnQueryTextListener {

  private RecyclerView recyclerView;
  private View loaderView;
  private HttpCallListAdapter httpCallListAdapter;
  private HttpClassSearchPresenter httpClassSearchPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_search);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ((SearchView)toolbar.findViewById(R.id.searchView)).setOnQueryTextListener(this);
    loaderView = findViewById(R.id.embedded_loader);
    recyclerView = (RecyclerView) findViewById(R.id.list);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    recyclerView.addItemDecoration(itemDecoration);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    httpCallListAdapter = new HttpCallListAdapter(new ArrayList<HttpCallRecord>(), this);
    recyclerView.setAdapter(httpCallListAdapter);
    httpClassSearchPresenter = new HttpClassSearchPresenter(new SnooperRepo(this), this, new BackgroundTaskExecutor(this));

  }

  @Override
  public void showResults(List<HttpCallRecord> httpCallRecords) {
    httpCallListAdapter.refreshData(httpCallRecords);
    httpCallListAdapter.notifyDataSetChanged();
    recyclerView.setVisibility(VISIBLE);
  }

  @Override
  public void showNoResultsFoundMessage(String keyword) {
    ((TextView)findViewById(R.id.no_results_found)).setText(getString(R.string.no_results_found, keyword));
    findViewById(R.id.no_results_found_container).setVisibility(VISIBLE);
  }

  @Override
  public void hideSearchResultsView() {
    recyclerView.setVisibility(GONE);
    findViewById(R.id.no_results_found_container).setVisibility(GONE);
  }

  @Override
  public void showLoader() {
    loaderView.setVisibility(VISIBLE);
  }

  @Override
  public void hideLoader() {
    loaderView.setVisibility(GONE);
  }

  @Override
  public void onClick(HttpCallRecord httpCall) {
    Intent intent = new Intent(this, HttpCallActivity.class);
    intent.putExtra(HTTP_CALL_ID, httpCall.getId());
    startActivity(intent);
    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    finish();
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String text) {
    httpClassSearchPresenter.searchCalls(text);
    return true;
  }
}
