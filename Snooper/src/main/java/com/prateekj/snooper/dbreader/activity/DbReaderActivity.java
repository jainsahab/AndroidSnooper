package com.prateekj.snooper.dbreader.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.DbFilesLocator;
import com.prateekj.snooper.dbreader.adapter.DatabaseAdapter;
import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity;

import java.util.List;

public class DbReaderActivity extends SnooperBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_db_reader);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.db_list);

    List<Database> databaseList = new DbFilesLocator(this).fetchApplicationDatabases();
    DatabaseAdapter adapter = new DatabaseAdapter(databaseList);

    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
  }
}
