package com.prateekj.snooper.dbreader.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.DatabaseReader;
import com.prateekj.snooper.dbreader.DatabaseDataReader;
import com.prateekj.snooper.dbreader.adapter.TableAdapter;
import com.prateekj.snooper.dbreader.adapter.TableEventListener;
import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbViewCallback;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.prateekj.snooper.dbreader.activity.DatabaseListActivity.DB_NAME;

public class DatabaseDetailActivity extends SnooperBaseActivity implements DbViewCallback, TableEventListener {
  public static final String TABLE_NAME = "TABLE_NAME";
  public static final String DB_PATH = "DB_PATH";
  private View embeddedLoader;
  private DatabaseReader databaseReader;
  private String dbPath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_db_view);
    initViews();
    dbPath = getIntent().getStringExtra(DatabaseListActivity.DB_PATH);
    String dbName = getIntent().getStringExtra(DB_NAME);
    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this);
    embeddedLoader = findViewById(R.id.embedded_loader);
    databaseReader = new DatabaseReader(this, backgroundTaskExecutor, new DatabaseDataReader());
    databaseReader.fetchDbContent(this, dbPath, dbName);
  }

  @Override
  public void onDbFetchStarted() {
    embeddedLoader.setVisibility(VISIBLE);
  }

  @Override
  public void onDbFetchCompleted(Database dbWithData) {
    embeddedLoader.setVisibility(GONE);
    updateDbView(dbWithData);
  }

  private void updateDbView(Database database) {
    TextView name = (TextView) findViewById(R.id.db_name);
    name.setText(database.getName());
    TextView version = (TextView) findViewById(R.id.db_version);
    version.setText(Integer.toString(database.getVersion()));
    updateTableList(database.getTables());
  }

  private void updateTableList(List<String> tables) {
    TableAdapter tableAdapter = new TableAdapter(tables, this);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.table_list);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(tableAdapter);
  }

  private void initViews() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void onTableClick(String table) {
    Intent dbViewActivity = new Intent(DatabaseDetailActivity.this, TableDetailActivity.class);
    dbViewActivity.putExtra(TABLE_NAME, table);
    dbViewActivity.putExtra(DB_PATH, dbPath);
    startActivity(dbViewActivity);
  }
}
