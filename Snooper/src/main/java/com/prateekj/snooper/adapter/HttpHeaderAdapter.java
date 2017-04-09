package com.prateekj.snooper.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.HeaderListItemBinding;
import com.prateekj.snooper.databinding.HeadersHeadingBinding;
import com.prateekj.snooper.model.HttpHeader;
import com.prateekj.snooper.viewmodel.HttpHeaderViewModel;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static com.prateekj.snooper.viewmodel.HttpHeaderViewModel.toViewModels;

public class HttpHeaderAdapter extends BaseAdapter implements StickyListHeadersAdapter {

  private List<HttpHeaderViewModel> viewModels;

  private HttpHeaderAdapter(List<HttpHeader> responseHeaders, List<HttpHeader> requestHeaders) {
    viewModels = new ArrayList<>();
    this.viewModels.addAll(toViewModels(responseHeaders, R.string.response_headers));
    this.viewModels.addAll(toViewModels(requestHeaders, R.string.request_headers));
  }

  @Override
  public View getHeaderView(int position, View convertView, ViewGroup parent) {
    HeadersHeadingBinding binding;
    if (convertView != null) {
      binding = DataBindingUtil.bind(convertView);
    } else {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      binding = DataBindingUtil.inflate(inflater, R.layout.headers_heading, parent, false);
    }
    binding.setText(parent.getContext().getString(this.viewModels.get(position).getHeaderId()));
    binding.executePendingBindings();
    return  binding.getRoot();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup viewGroup) {
    HeaderListItemBinding binding;
    if (convertView != null) {
      binding = DataBindingUtil.bind(convertView);
    } else {
      LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
      binding = DataBindingUtil.inflate(inflater, R.layout.header_list_item, viewGroup, false);
    }
    binding.setViewModel(this.viewModels.get(position));
    binding.executePendingBindings();
    return binding.getRoot();
  }

  @Override
  public long getHeaderId(int position) {
    return this.viewModels.get(position).getHeaderId();
  }

  @Override
  public int getCount() {
    return this.viewModels.size();
  }

  @Override
  public Object getItem(int pos) {
    return this.viewModels.get(pos);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public static HttpHeaderAdapter newInstance(List<HttpHeader> responseHeaders, List<HttpHeader> requestHeaders) {
    return new HttpHeaderAdapter(responseHeaders, requestHeaders);
  }
}
