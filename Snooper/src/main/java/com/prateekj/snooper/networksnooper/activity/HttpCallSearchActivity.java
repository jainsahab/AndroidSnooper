package com.prateekj.snooper.networksnooper.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prateekj.snooper.R;

public class HttpCallSearchActivity extends SnooperBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_call_search);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    setupSearchbar(toolbar);
  }

  private void setupSearchbar(Toolbar toolbar) {
    ((ViewGroup.MarginLayoutParams)findView(toolbar, "android:id/search_edit_frame").getLayoutParams()).setMarginStart(0);
    ((ViewGroup.MarginLayoutParams)findView(toolbar, "android:id/search_edit_frame").getLayoutParams()).setMarginEnd(0);
    View searchIcon = findView(toolbar, "android:id/search_mag_icon");
    ((ViewGroup.MarginLayoutParams) searchIcon.getLayoutParams()).setMarginStart(0);
    ((ViewGroup.MarginLayoutParams) searchIcon.getLayoutParams()).setMarginEnd(0);
    ((ImageView) searchIcon).setImageDrawable(getResources().getDrawable(R.drawable.white_back_arrow));
    searchIcon.setOnClickListener(getBackPressedListener());
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
}
