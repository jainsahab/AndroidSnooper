package com.prateekj.snooper.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
  public static final int VERTICAL = LinearLayoutManager.VERTICAL;
  private Drawable mDivider;
  private int mOrientation;

  public DividerItemDecoration(Context context, int orientation, int drawableId) {
    mDivider = ContextCompat.getDrawable(context, drawableId);
    setOrientation(orientation);
  }

  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("invalid orientation");
    }
    mOrientation = orientation;
  }

  @Override
  public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    if (mOrientation == VERTICAL) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  private void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
        .getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  private void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
        .getLayoutParams();
      final int left = child.getRight() + params.rightMargin;
      final int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    if (mOrientation == VERTICAL) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }
}
