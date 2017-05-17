package com.prateekj.snooper.networksnooper.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.base.Function;
import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel;

import java.util.List;

import static android.view.LayoutInflater.from;
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
    HttpHeaderViewModel viewModel = viewModels.get(position);
    View view = convertView != null ? convertView : newView(parent);
    return bindView(view, viewModel);
  }

  private View newView(ViewGroup parent) {
    return from(parent.getContext()).inflate(R.layout.header_list_item, parent, false);
  }

  private View bindView(View view, HttpHeaderViewModel viewModel) {
    ((TextView)view.findViewById(R.id.header_name)).setText(viewModel.headerName());
    ((TextView)view.findViewById(R.id.header_value)).setText(viewModel.headerValues());
    return view;
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
