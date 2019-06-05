package com.prateekj.snooper.networksnooper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.prateekj.snooper.R;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.customviews.NextPageRequestListener;
import com.prateekj.snooper.customviews.PaginatedRecyclerView;
import com.prateekj.snooper.networksnooper.adapter.HttpCallListAdapter;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.presenter.HttpCallListPresenter;
import com.prateekj.snooper.networksnooper.views.HttpListView;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.networksnooper.presenter.HttpCallListPresenter.PAGE_SIZE;

public class HttpCallListActivity extends SnooperBaseActivity implements HttpListView, NextPageRequestListener{

  private HttpCallListPresenter presenter;
  private HttpCallListAdapter httpCallListAdapter;
  private SnooperRepo repo;
  private PaginatedRecyclerView httpCallList;
  private boolean areAllPagesLoaded;
  private boolean noCallsFound;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    repo = new SnooperRepo(this);
    presenter = new HttpCallListPresenter(this, repo);
    httpCallList = (PaginatedRecyclerView) findViewById(R.id.list);
    httpCallList.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL, R.drawable.grey_divider);
    httpCallList.addItemDecoration(itemDecoration);
    httpCallList.setItemAnimator(new DefaultItemAnimator());
    httpCallList.setNextPageListener(this);
    presenter.init();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.http_call_list_menu, menu);
    if (noCallsFound) {
      menu.findItem(R.id.delete_records_menu).setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.done_menu) {
      presenter.onDoneClick();
      return true;
    } else if (item.getItemId() == R.id.delete_records_menu) {
      presenter.onDeleteRecordsClicked();
    } else if (item.getItemId() == R.id.search) {
      openSearchActivity();
    }
    return super.onOptionsItemSelected(item);
  }

  private void openSearchActivity() {
    startActivity(new Intent(this, HttpCallSearchActivity.class));
  }


  @Override
  public void navigateToResponseBody(long httpCallId) {
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
  public void updateListViewAfterDelete() {
    httpCallListAdapter.refreshData(repo.findAllSortByDateAfter(-1, 20));
    httpCallListAdapter.notifyDataSetChanged();
  }

  @Override
  public void initHttpCallRecordList(List<HttpCallRecord> httpCallRecords) {
    httpCallListAdapter = new HttpCallListAdapter(httpCallRecords, presenter);
    checkIfAllPagesAreLoaded(httpCallRecords);
    httpCallList.setAdapter(httpCallListAdapter);
  }

  @Override
  public void appendRecordList(List<HttpCallRecord> httpCallRecords) {
    httpCallListAdapter.appendData(httpCallRecords);
    checkIfAllPagesAreLoaded(httpCallRecords);
    httpCallList.post(new Runnable() {
      @Override
      public void run() {
        httpCallListAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void renderNoCallsFoundView() {
    noCallsFound = true;
    findViewById(R.id.http_call_list_container).setVisibility(GONE);
    findViewById(R.id.no_calls_found_container).setVisibility(VISIBLE);
  }

  @Override
  public void requestNextPage() {
    presenter.onNextPageCall();
  }

  @Override
  public boolean areAllPagesLoaded() {
    return areAllPagesLoaded;
  }

  private void checkIfAllPagesAreLoaded(List<HttpCallRecord> httpCallRecords) {
    if (httpCallRecords.size() < PAGE_SIZE) {
      areAllPagesLoaded = true;
    }
  }
}
