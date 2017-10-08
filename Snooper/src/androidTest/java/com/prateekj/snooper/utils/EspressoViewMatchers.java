package com.prateekj.snooper.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import static java.text.MessageFormat.format;

public class EspressoViewMatchers {

  public static Matcher<View> withRecyclerView(final int recyclerViewId, final int position) {
    return new CustomTypeSafeMatcher<View>(format("recycler view with id: {0} at position: {1}",
      recyclerViewId, position)) {
      @Override
      protected boolean matchesSafely(View item) {
        View view = item.getRootView().findViewById(recyclerViewId);

        if (view == null || !(view instanceof RecyclerView))
          return false;
        RecyclerView recyclerView = (RecyclerView) view;
        View childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
        return childView == item;
      }
    };
  }

  public static Matcher<View> withTableLayout(final int tableLayoutId, final int row, final int column) {
    return new CustomTypeSafeMatcher<View>(format("Table layout with id: {0} at row: {1} and column: {2}",
      tableLayoutId, row, column)) {
      @Override
      protected boolean matchesSafely(View item) {
        View view = item.getRootView().findViewById(tableLayoutId);

        if (view == null || !(view instanceof TableLayout))
          return false;
        TableLayout tableLayout = (TableLayout) view;
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        View childView = tableRow.getChildAt(column);
        return childView == item;
      }
    };
  }

  public static Matcher<View> withListSize(final int size) {
    return new CustomTypeSafeMatcher<View>(format("recycler view with id: {0} ",
      size)) {
      @Override
      protected boolean matchesSafely(View view) {

        if (view == null || !(view instanceof RecyclerView))
          return false;
        RecyclerView recyclerView = (RecyclerView) view;
        return recyclerView.getAdapter().getItemCount() == size;
      }
    };
  }
}
