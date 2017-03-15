package com.prateekj.snooper.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
}
