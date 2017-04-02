package com.prateekj.snooper.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.prateekj.snooper.R;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.fragment.HttpCallFragment;
import com.prateekj.snooper.fragment.HttpHeadersFragment;
import com.prateekj.snooper.presenter.HttpCallPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.views.HttpCallView;

public class HttpCallActivity extends AppCompatActivity implements HttpCallView{

  public static String HTTP_CALL_ID = "HTTP_CALL_ID";
  public static String HTTP_CALL_MODE = "HTTP_CALL_MODE";
  public static int REQUEST_MODE = 1;
  public static int RESPONSE_MODE = 2;
  private HttpCallPresenter httpCallPresenter;
  private ViewPager pager;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    int httpCallId = getIntent().getIntExtra(HTTP_CALL_ID, 0);
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(this));
    httpCallPresenter = new HttpCallPresenter(httpCallId, repo, this, new ResponseFormatterFactory());
    progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setIndeterminate(true);
    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
    progressDialog.setMessage("Just a Moment....");
    progressDialog.show();
    setupUi();
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
  public void onHttpCallBodyFormatted(int mode) {
    httpCallPresenter.onHttpCallBodyFormatted(mode);
  }

  @Override
  public void dismissProgressDialog() {
    progressDialog.dismiss();
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
