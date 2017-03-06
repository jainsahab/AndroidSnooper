package com.prateekj.snooper.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.prateekj.snooper.R;
import com.prateekj.snooper.fragment.HttpBodyFragment;

public class HttpCallActivity extends AppCompatActivity {

  public static String HTTP_CALL_ID = "HTTP_CALL_ID";
  public static String HTTP_CALL_MODE = "HTTP_CALL_MODE";
  public static int REQUEST_MODE = 1;
  public static int RESPONSE_MODE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final ViewPager pager = (ViewPager) findViewById(R.id.pager);
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    tabLayout.addTab(tabLayout.newTab().setText(R.string.response));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.request));
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

  private HttpBodyFragment getResponseBodyFragment() {
    HttpBodyFragment fragment = new HttpBodyFragment();
    Bundle extras = getIntent().getExtras();
    extras.putInt(HTTP_CALL_MODE, RESPONSE_MODE);
    fragment.setArguments(extras);
    return fragment;
  }

  private HttpBodyFragment getRequestBodyFragment() {
    HttpBodyFragment fragment = new HttpBodyFragment();
    Bundle extras = getIntent().getExtras();
    extras.putInt(HTTP_CALL_MODE, REQUEST_MODE);
    fragment.setArguments(extras);
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
      return getRequestBodyFragment();
    }

    @Override
    public int getCount() {
      return 2;
    }
  }
}
