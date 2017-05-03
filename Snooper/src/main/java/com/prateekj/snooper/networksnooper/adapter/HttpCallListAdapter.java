package com.prateekj.snooper.networksnooper.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.ActivityHttpCallListItemBinding;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;

import java.util.List;

public class HttpCallListAdapter extends RecyclerView.Adapter<HttpCallListAdapter.HttpCallViewHolder> {


  private final List<HttpCall> httpCalls;
  private HttpCallListClickListener listener;

  public HttpCallListAdapter(SnooperRepo repo, HttpCallListClickListener listener) {
    this.listener = listener;
    httpCalls = repo.findAll();
  }

  public class HttpCallViewHolder extends RecyclerView.ViewHolder {
    private ActivityHttpCallListItemBinding binding;

    public HttpCallViewHolder(ActivityHttpCallListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(HttpCall httpCall) {
      HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
      this.binding.setHttpCallViewModel(httpCallViewModel);
      this.binding.executePendingBindings();
      setClickListener(httpCall);
    }

    private void setClickListener(final HttpCall httpCall) {
      this.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onClick(httpCall);
        }
      });
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
