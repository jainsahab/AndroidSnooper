package com.prateekj.snooper.networksnooper.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.prateekj.snooper.R;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.AppPermissionChecker;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.fragment.HttpCallFragment;
import com.prateekj.snooper.networksnooper.fragment.HttpHeadersFragment;
import com.prateekj.snooper.networksnooper.presenter.HttpCallPresenter;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.views.HttpCallView;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.utils.FileUtil;

import java.io.File;

import static android.content.Intent.EXTRA_SUBJECT;

public class HttpCallActivity extends SnooperBaseActivity implements HttpCallView {

  public static final String HTTP_CALL_ID = "HTTP_CALL_ID";
  public static final String HTTP_CALL_MODE = "HTTP_CALL_MODE";
  public static final int REQUEST_MODE = 1;
  public static final int RESPONSE_MODE = 2;
  public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
  private static final String LOGFILE_MIME_TYPE = "*/*";
  private HttpCallPresenter httpCallPresenter;
  private ViewPager pager;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    int httpCallId = getIntent().getIntExtra(HTTP_CALL_ID, 0);
    FileUtil fileUtil = new FileUtil();
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this);
    httpCallPresenter = new HttpCallPresenter(httpCallId, repo, this, new ResponseFormatterFactory(), fileUtil, backgroundTaskExecutor);
    initializeProgressDialog();
    setupUi();
  }

  private void initializeProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setIndeterminate(true);
    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
    progressDialog.setMessage(getString(R.string.progress_wait_message));
    progressDialog.show();
  }

  private void setupUi() {
    pager = (ViewPager) findViewById(R.id.pager);
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    for (HttpCallTab tab : HttpCallTab.sortedValues()) {
      tabLayout.addTab(tabLayout.newTab().setText(tab.getTabTitle()));
    }
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    HttpCallPagerAdapter adapter = new HttpCallPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(adapter);
    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        pager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(Tab tab) {

      }

      @Override
      public void onTabReselected(Tab tab) {

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.http_call_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.copy_menu) {
      httpCallPresenter.copyHttpCallBody(pager.getCurrentItem());
      return true;
    } else if (item.getItemId() == R.id.share_menu) {
      shareHttpCallData();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void copyToClipboard(String data) {
    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("Copied", data);
    clipboard.setPrimaryClip(clip);
  }

  @Override
  public void shareData(String logFilePath) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    File file = new File(logFilePath);
    Uri fileUri = Uri.fromFile(file);
    intent.setData(fileUri);
    intent.setType(LOGFILE_MIME_TYPE);
    intent.putExtra(EXTRA_SUBJECT,
      getString(R.string.mail_subject_share_logs));
    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
    Intent j = Intent.createChooser(intent, getString(R.string.chooser_title_share_logs));
    startActivity(j);
  }

  @Override
  public void showMessageShareNotAvailable() {
    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onHttpCallBodyFormatted(int mode) {
    httpCallPresenter.onHttpCallBodyFormatted(mode);
  }

  @Override
  public void dismissProgressDialog() {
    progressDialog.dismiss();
  }

  private void shareHttpCallData() {
    appPermissionChecker.handlePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_REQUEST_CODE, new AppPermissionChecker.PermissionRequestCallBack() {
      @Override
      public void permissionGranted() {
        httpCallPresenter.shareHttpCallBody();
      }

      @Override
      public void permissionDenied() {
        httpCallPresenter.onPermissionDenied();
      }
    });
  }

  private HttpCallFragment getResponseBodyFragment() {
    HttpCallFragment fragment = new HttpCallFragment();
    Bundle extras = getIntent().getExtras();
    extras.putInt(HTTP_CALL_MODE, RESPONSE_MODE);
    fragment.setArguments(extras);
    return fragment;
  }

  private HttpCallFragment getRequestBodyFragment() {
    HttpCallFragment fragment = new HttpCallFragment();
    Bundle extras = getIntent().getExtras();
    extras.putInt(HTTP_CALL_MODE, REQUEST_MODE);
    fragment.setArguments(extras);
    return fragment;
  }

  private HttpHeadersFragment getHeadersFragment() {
    HttpHeadersFragment fragment = new HttpHeadersFragment();
    fragment.setArguments(getIntent().getExtras());
    return fragment;
  }

  private class HttpCallPagerAdapter extends FragmentStatePagerAdapter {

    public HttpCallPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      if (position == 0)
        return getResponseBodyFragment();
      if (position == 1)
        return getRequestBodyFragment();
      return getHeadersFragment();
    }

    @Override
    public int getCount() {
      return HttpCallTab.values().length;
    }
  }
}
