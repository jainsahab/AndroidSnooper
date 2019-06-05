package com.prateekj.snooper.customviews;


import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

public class PaginatedRecyclerView extends RecyclerView implements PageAddedListener {

  private NextPageRequestListener listener;
  private boolean loading;

  public PaginatedRecyclerView(Context context) {
    super(context);
  }

  public PaginatedRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public PaginatedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setAdapter(Adapter adapter) {
    super.setAdapter(adapter);
    adapter.registerAdapterDataObserver(new AdapterDataAppendObserver(this));
  }

  public void setNextPageListener(NextPageRequestListener listener) {
    this.listener = listener;
  }

  @Override
  public void onScrolled(int dx, int dy) {
    super.onScrolled(dx, dy);
    if (loading || listener == null) {
      return;
    }
    if (needToRequestNextPage() && !listener.areAllPagesLoaded()) {
      loading = true;
      listener.requestNextPage();
    }
  }

  private boolean needToRequestNextPage() {
    LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
    int visibleItemCount = layoutManager.getChildCount();
    int totalItemCount = getAdapter().getItemCount();
    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
    return firstVisibleItemPosition > 0 && firstVisibleItemPosition + visibleItemCount > totalItemCount - 5;
  }

  @Override
  public void onPageAdded() {
    loading = false;
  }
}


