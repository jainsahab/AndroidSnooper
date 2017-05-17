package com.prateekj.snooper.networksnooper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;

import java.util.List;

import static com.prateekj.snooper.utils.UIUtils.setTextColor;

public class HttpCallListAdapter extends RecyclerView.Adapter<HttpCallListAdapter.HttpCallViewHolder> {


  private final List<HttpCall> httpCalls;
  private HttpCallListClickListener listener;

  public HttpCallListAdapter(SnooperRepo repo, HttpCallListClickListener listener) {
    this.listener = listener;
    httpCalls = repo.findAll();
  }

  public class HttpCallViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public HttpCallViewHolder(View view) {
      super(view);
      this.view = view;
    }

    public void bind(HttpCall httpCall) {
      HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
      ((TextView) view.findViewById(R.id.url)).setText(httpCallViewModel.getUrl());
      ((TextView) view.findViewById(R.id.method)).setText(httpCallViewModel.getMethod());
      ((TextView) view.findViewById(R.id.status_code)).setText(httpCallViewModel.getStatusCode());
      ((TextView) view.findViewById(R.id.status_text)).setText(httpCallViewModel.getStatusText());
      ((TextView) view.findViewById(R.id.time_stamp)).setText(httpCallViewModel.getTimeStamp());
      setTextColor((TextView) view.findViewById(R.id.method), httpCallViewModel.getStatusColor());
      setTextColor((TextView) view.findViewById(R.id.status_code), httpCallViewModel.getStatusColor());
      setTextColor((TextView) view.findViewById(R.id.status_text), httpCallViewModel.getStatusColor());
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
    View listItemView = inflater.inflate(R.layout.activity_http_call_list_item, parent, false);
    return new HttpCallViewHolder(listItemView);
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
