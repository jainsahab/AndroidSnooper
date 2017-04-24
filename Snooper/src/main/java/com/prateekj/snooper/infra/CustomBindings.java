package com.prateekj.snooper.infra;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CustomBindings {

  @BindingAdapter("app:adapter")
  public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
    recyclerView.setAdapter(adapter);
  }

  @BindingAdapter("app:stickyAdapter")
  public static void setStickyAdapter(StickyListHeadersListView stickyHeadersListView, StickyListHeadersAdapter adapter) {
    stickyHeadersListView.setAdapter(adapter);
  }

  @BindingAdapter("app:itemDecoration")
  public static void setItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
    recyclerView.addItemDecoration(itemDecoration);
  }
}
