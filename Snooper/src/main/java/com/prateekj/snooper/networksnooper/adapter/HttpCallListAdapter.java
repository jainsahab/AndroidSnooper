package com.prateekj.snooper.networksnooper.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;

import java.util.List;

import static com.prateekj.snooper.utils.UIUtils.setTextColor;

@SuppressWarnings("ResourceType")
public class HttpCallListAdapter extends RecyclerView.Adapter<HttpCallListAdapter.HttpCallViewHolder> {


  private List<HttpCallRecord> httpCallRecords;
  private HttpCallListClickListener listener;

  public HttpCallListAdapter(List<HttpCallRecord> httpCallRecords, HttpCallListClickListener listener) {
    this.listener = listener;
    this.httpCallRecords = httpCallRecords;
  }

  class HttpCallViewHolder extends RecyclerView.ViewHolder {
    private View view;

    HttpCallViewHolder(View view) {
      super(view);
      this.view = view;
    }

    void bind(HttpCallRecord httpCall) {
      HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
      ((TextView) view.findViewById(R.id.url)).setText(httpCallViewModel.getUrl());
      ((TextView) view.findViewById(R.id.method)).setText(httpCallViewModel.getMethod());
      ((TextView) view.findViewById(R.id.status_code)).setText(httpCallViewModel.getStatusCode());
      ((TextView) view.findViewById(R.id.status_text)).setText(httpCallViewModel.getStatusText());
      ((TextView) view.findViewById(R.id.time_stamp)).setText(httpCallViewModel.getTimeStamp());
      view.findViewById(R.id.response_info_container).setVisibility(httpCallViewModel.getResponseInfoVisibility());
      view.findViewById(R.id.error_text).setVisibility(httpCallViewModel.getFailedTextVisibility());
      setTextColor((TextView) view.findViewById(R.id.method), httpCallViewModel.getStatusColor());
      setTextColor((TextView) view.findViewById(R.id.status_code), httpCallViewModel.getStatusColor());
      setTextColor((TextView) view.findViewById(R.id.status_text), httpCallViewModel.getStatusColor());
      setClickListener(httpCall);
    }

    private void setClickListener(final HttpCallRecord httpCall) {
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
    holder.bind(httpCallRecords.get(position));
  }

  @Override
  public int getItemCount() {
    return httpCallRecords.size();
  }

  public void refreshData(List<HttpCallRecord> httpCallRecords) {
    this.httpCallRecords = httpCallRecords;
  }

  public void appendData(List<HttpCallRecord> httpCallRecords) {
    this.httpCallRecords.addAll(httpCallRecords);
  }
}
