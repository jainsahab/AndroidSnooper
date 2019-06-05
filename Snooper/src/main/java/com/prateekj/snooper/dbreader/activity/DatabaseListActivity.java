package com.prateekj.snooper.dbreader.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.DatabaseReader;
import com.prateekj.snooper.dbreader.DatabaseDataReader;
import com.prateekj.snooper.dbreader.adapter.DatabaseAdapter;
import com.prateekj.snooper.dbreader.adapter.DbEventListener;
import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DatabaseListActivity extends SnooperBaseActivity implements DbReaderCallback, DbEventListener {
  public static final String DB_PATH = "DB_PATH";
  public static final String DB_NAME = "DB_NAME";
  private RecyclerView recyclerView;
  private DatabaseAdapter adapter;
  private View embeddedLoader;
  private DatabaseReader databaseReader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_db_reader);
    initViews();

    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this);
    databaseReader = new DatabaseReader(this, backgroundTaskExecutor, new DatabaseDataReader());
    databaseReader.fetchApplicationDatabases(this);
  }

  @Override
  public void onDbFetchStarted() {
    embeddedLoader.setVisibility(VISIBLE);
  }

  @Override
  public void onApplicationDbFetchCompleted(List<Database> databases) {
    embeddedLoader.setVisibility(GONE);
    adapter = new DatabaseAdapter(databases, this);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.db_list);

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onDatabaseClick(Database db) {
    Intent dbViewActivity = new Intent(DatabaseListActivity.this, DatabaseDetailActivity.class);
    dbViewActivity.putExtra(DB_PATH, db.getPath());
    dbViewActivity.putExtra(DB_NAME, db.getName());
    startActivity(dbViewActivity);
  }

  private void initViews() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    recyclerView = (RecyclerView) findViewById(R.id.db_list);
    embeddedLoader = findViewById(R.id.embedded_loader);
  }
}
