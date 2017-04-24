package com.prateekj.snooper.infra;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomBindings {

  @BindingAdapter("adapter")
  public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
    recyclerView.setAdapter(adapter);
  }

  @BindingAdapter("listAdapter")
  public static void setListAdapter(ListView listView, BaseAdapter adapter) {
    listView.setAdapter(adapter);
  }

  @BindingAdapter("itemDecoration")
  public static void setItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
    recyclerView.addItemDecoration(itemDecoration);
  }

  @BindingAdapter({"textColor"})
  public static void setTextColor(TextView view, int color) {
    if (color != 0) {
      view.setTextColor(view.getResources().getColor(color));
    }
  }

}
