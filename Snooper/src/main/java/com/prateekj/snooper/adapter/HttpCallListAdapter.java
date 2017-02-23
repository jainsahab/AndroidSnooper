package com.prateekj.snooper.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.prateekj.snooper.HttpCall;
import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.ActivityHttpCallListItemBinding;
import com.prateekj.snooper.repo.SnooperRepo;

import java.util.List;

public class HttpCallListAdapter extends RecyclerView.Adapter<HttpCallListAdapter.HttpCallViewHolder>{


  private final List<HttpCall> httpCalls;

  public HttpCallListAdapter(SnooperRepo repo) {
    httpCalls = repo.findAll();
  }

  public static class HttpCallViewHolder extends RecyclerView.ViewHolder {
    private ActivityHttpCallListItemBinding binding;

    public HttpCallViewHolder(ActivityHttpCallListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(HttpCall httpCall) {
      this.binding.setHttpCall(httpCall);
      this.binding.executePendingBindings();
    }

  }
  @Override
  public HttpCallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    ActivityHttpCallListItemBinding binding = DataBindingUtil.inflate(
        inflater, R.layout.activity_http_call_list_item, parent, false);
    return new HttpCallViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(HttpCallViewHolder holder, int position) {
    holder.bind(httpCalls.get(position));
  }

  @Override
  public int getItemCount() {
    return httpCalls.size();
  }
}
