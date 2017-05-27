package com.prateekj.snooper.networksnooper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.networksnooper.presenter.HttpCallListPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.views.HttpListView;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class HttpCallListActivity extends SnooperBaseActivity implements HttpListView {

  private HttpCallListPresenter presenter;
  private HttpCallListAdapter httpCallListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    presenter = new HttpCallListPresenter(this, repo);
    RecyclerView httpCallList = (RecyclerView) findViewById(R.id.list);
    httpCallListAdapter = new HttpCallListAdapter(repo, presenter);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    httpCallList.addItemDecoration(itemDecoration);
    httpCallList.setItemAnimator(new DefaultItemAnimator());
    httpCallList.setAdapter(httpCallListAdapter);
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
    } else if (item.getItemId() == R.id.delete_records_menu) {
      presenter.onDeleteRecordsClicked();
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

  @Override
  public void showDeleteConfirmationDialog() {
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            presenter.confirmDeleteRecords();
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            dialog.dismiss();
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.delete_records_dialog_text).setPositiveButton(getString(R.string.delete_records_dialog_confirmation), dialogClickListener)
      .setNegativeButton(getString(R.string.delete_records_dialog_cancellation), dialogClickListener).show();
  }

  @Override
  public void updateListView() {
    httpCallListAdapter.notifyDataSetChanged();
  }
}
