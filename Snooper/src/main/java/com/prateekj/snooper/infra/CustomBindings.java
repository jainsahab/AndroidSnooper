package com.prateekj.snooper.infra;

import android.databinding.BindingAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CustomBindings {

  @BindingAdapter("adapter")
  public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
    recyclerView.setAdapter(adapter);
  }

  @BindingAdapter("stickyAdapter")
  public static void setStickyAdapter(StickyListHeadersListView stickyHeadersListView, StickyListHeadersAdapter adapter) {
    stickyHeadersListView.setAdapter(adapter);
  }

  @BindingAdapter("nestingScrollingEnabled")
  public static void setNestingScrollingEnabled(StickyListHeadersListView stickyHeadersListView, boolean enabled) {
    ListView listView = stickyHeadersListView.getWrappedList();
    ViewCompat.setNestedScrollingEnabled(listView, enabled);
  }

  @BindingAdapter("itemDecoration")
  public static void setItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
    recyclerView.addItemDecoration(itemDecoration);
  }
}
