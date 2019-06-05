package com.prateekj.snooper.customviews;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterDataAppendObserver extends RecyclerView.AdapterDataObserver {

  private PageAddedListener listener;

  public AdapterDataAppendObserver(PageAddedListener listener) {
    this.listener = listener;
  }

  @Override
  public void onChanged() {
    super.onChanged();
    listener.onPageAdded();
  }
}
