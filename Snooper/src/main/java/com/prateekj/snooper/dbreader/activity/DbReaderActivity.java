package com.prateekj.snooper.dbreader.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.DbFilesLocator;
import com.prateekj.snooper.dbreader.adapter.DatabaseAdapter;
import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DbReaderActivity extends SnooperBaseActivity implements DbReaderCallback {
  private RecyclerView recyclerView;
  private DatabaseAdapter adapter;
  private View embeddedLoader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_db_reader);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this);
    embeddedLoader = findViewById(R.id.embedded_loader);
    new DbFilesLocator(this, backgroundTaskExecutor, this).fetchApplicationDatabases();
  }

  @Override
  public void onDbFetchStarted() {
    embeddedLoader.setVisibility(VISIBLE);
  }

  @Override
  public void onDbFetchCompleted(List<Database> databases) {
    embeddedLoader.setVisibility(GONE);
    adapter = new DatabaseAdapter(databases);

    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
  }
}
