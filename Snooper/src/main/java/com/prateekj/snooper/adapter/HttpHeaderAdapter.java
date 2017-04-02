package com.prateekj.snooper.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.HeaderListItemBinding;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.viewmodel.HttpHeaderViewModel;

import java.util.List;

import static com.google.common.collect.Lists.transform;

public class HttpHeaderAdapter extends RecyclerView.Adapter<HttpHeaderAdapter.HttpHeaderViewHolder> {

  private List<HttpHeaderViewModel> viewModels;

  private HttpHeaderAdapter(List<HttpHeader> headers) {
    this.viewModels = toViewModels(headers);
  }

  @Override
  public HttpHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    HeaderListItemBinding binding = DataBindingUtil.inflate(
      inflater, R.layout.header_list_item, parent, false);
    return new HttpHeaderViewHolder(binding);

  }

  @Override
  public void onBindViewHolder(HttpHeaderViewHolder holder, int position) {
    holder.bind(viewModels.get(position));
  }

  @Override
  public int getItemCount() {
    return this.viewModels.size();
  }

  private List<HttpHeaderViewModel> toViewModels(List<HttpHeader> headers) {
    return transform(headers, new Function<HttpHeader, HttpHeaderViewModel>() {
      @Override
      public HttpHeaderViewModel apply(HttpHeader httpHeader) {
        return new HttpHeaderViewModel(httpHeader);
      }
    });
  }

  public static HttpHeaderAdapter from(List<HttpHeader> headers) {
    return new HttpHeaderAdapter(headers);
  }

  public class HttpHeaderViewHolder extends RecyclerView.ViewHolder {

    private HeaderListItemBinding binding;

    public HttpHeaderViewHolder(HeaderListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(HttpHeaderViewModel httpHeaderViewModel) {
      this.binding.setViewModel(httpHeaderViewModel);
      this.binding.executePendingBindings();
    }
  }
}
