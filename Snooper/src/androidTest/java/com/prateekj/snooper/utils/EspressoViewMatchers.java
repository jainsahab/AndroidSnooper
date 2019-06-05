package com.prateekj.snooper.utils;

import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

  public static Matcher<View> hasBackgroundSpanOn(final String text, @ColorRes final int colorResource) {
    return new CustomTypeSafeMatcher<View>("") {
      @Override
      protected boolean matchesSafely(View view) {
        if (view == null || !(view instanceof TextView))
          return false;
        SpannableString spannableString = (SpannableString) ((TextView) view).getText();
        BackgroundColorSpan[] spans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
          int start = spannableString.getSpanStart(span);
          int end = spannableString.getSpanEnd(span);
          CharSequence highlightedString = spannableString.subSequence(start, end);
          if (text.equals(highlightedString.toString())) {
            return span.getBackgroundColor() == view.getContext().getResources().getColor(colorResource);
          }
        }
        return false;
      }
    };
  }
}
