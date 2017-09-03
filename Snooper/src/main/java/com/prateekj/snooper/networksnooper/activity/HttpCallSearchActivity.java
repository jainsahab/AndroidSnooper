package com.prateekj.snooper.networksnooper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

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
    setupSearchBar(toolbar);
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

  private void setupSearchBar(Toolbar toolbar) {
    ((ViewGroup.MarginLayoutParams)findView(toolbar, "android:id/search_edit_frame").getLayoutParams()).setMarginStart(0);
    ((ViewGroup.MarginLayoutParams)findView(toolbar, "android:id/search_edit_frame").getLayoutParams()).setMarginEnd(0);
    View searchIcon = findView(toolbar, "android:id/search_mag_icon");
    ((ViewGroup.MarginLayoutParams) searchIcon.getLayoutParams()).setMarginStart(0);
    ((ViewGroup.MarginLayoutParams) searchIcon.getLayoutParams()).setMarginEnd(0);
    ((ImageView) searchIcon).setImageDrawable(getResources().getDrawable(R.drawable.white_back_arrow));
    searchIcon.setOnClickListener(getBackPressedListener());
    ((SearchView)toolbar.findViewById(R.id.searchView)).setOnQueryTextListener(this);
  }

  private View findView(Toolbar toolbar, String id) {
    return toolbar.findViewById(toolbar.getContext().getResources().getIdentifier(id, null, null));
  }

  public View.OnClickListener getBackPressedListener() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    };
  }

  @Override
  public void showResults(List<HttpCallRecord> httpCallRecords) {
    httpCallListAdapter.refreshData(httpCallRecords);
    httpCallListAdapter.notifyDataSetChanged();
    recyclerView.setVisibility(VISIBLE);
  }

  @Override
  public void hideResultList() {
    recyclerView.setVisibility(GONE);
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
