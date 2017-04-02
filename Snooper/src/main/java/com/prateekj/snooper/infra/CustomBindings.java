package com.prateekj.snooper.infra;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

public class CustomBindings {

  @BindingAdapter("app:adapter")
  public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
    recyclerView.setAdapter(adapter);
  }

  @BindingAdapter("app:itemDecoration")
  public static void setItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
    recyclerView.addItemDecoration(itemDecoration);
  }
}
