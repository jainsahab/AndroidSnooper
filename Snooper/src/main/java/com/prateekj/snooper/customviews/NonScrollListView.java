package com.prateekj.snooper.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class NonScrollListView extends ListView {
  public NonScrollListView(Context context) {
    super(context);
  }

  public NonScrollListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NonScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(LOLLIPOP)
  public NonScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
      Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
    ViewGroup.LayoutParams params = getLayoutParams();
    params.height = getMeasuredHeight();
  }
}
