package com.prateekj.snooper.networksnooper.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.base.Function;
import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.HeaderListItemBinding;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel;

import java.util.List;

import static com.google.common.collect.Lists.transform;

public class HttpHeaderAdapter extends BaseAdapter {


  private final List<HttpHeaderViewModel> viewModels;

  private HttpHeaderAdapter(List<HttpHeader> headers) {
    viewModels = toViewModels(headers);
  }

  @Override
  public int getCount() {
    return viewModels.size();
  }

  @Override
  public Object getItem(int position) {
    return viewModels.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    HeaderListItemBinding binding;
    if (convertView != null) {
      binding = DataBindingUtil.bind(convertView);
    } else {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      binding = DataBindingUtil.inflate(inflater, R.layout.header_list_item, parent, false);
    }
    binding.setViewModel(viewModels.get(position));
    binding.executePendingBindings();
    return binding.getRoot();
  }


  private List<HttpHeaderViewModel> toViewModels(List<HttpHeader> headers) {
    return transform(headers, new Function<HttpHeader, HttpHeaderViewModel>() {
      @Override
      public HttpHeaderViewModel apply(HttpHeader httpHeader) {
        return new HttpHeaderViewModel(httpHeader);
      }
    });
  }

  public static HttpHeaderAdapter newInstance(List<HttpHeader> headers) {
    return new HttpHeaderAdapter(headers);
  }
}
