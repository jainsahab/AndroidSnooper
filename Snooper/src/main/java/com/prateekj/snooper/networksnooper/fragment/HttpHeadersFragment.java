package com.prateekj.snooper.networksnooper.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.customviews.NonScrollListView;
import com.prateekj.snooper.networksnooper.adapter.HttpHeaderAdapter;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;

@SuppressWarnings("ResourceType")
public class HttpHeadersFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_headers, container, false);
    SnooperRepo snooperRepo = new SnooperRepo(getActivity());
    long httpCallId = getArguments().getLong(HTTP_CALL_ID);
    HttpCallRecord httpCallRecord = snooperRepo.findById(httpCallId);
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCallRecord);
    ((TextView)view.findViewById(R.id.url)).setText(httpCallViewModel.getUrl());
    ((TextView)view.findViewById(R.id.method)).setText(httpCallViewModel.getMethod());
    ((TextView)view.findViewById(R.id.status_code)).setText(httpCallViewModel.getStatusCode());
    ((TextView)view.findViewById(R.id.status_text)).setText(httpCallViewModel.getStatusText());
    ((TextView)view.findViewById(R.id.time_stamp)).setText(httpCallViewModel.getTimeStamp());
    view.findViewById(R.id.response_info_container).setVisibility(httpCallViewModel.getResponseInfoVisibility());
    view.findViewById(R.id.error_text).setVisibility(httpCallViewModel.getFailedTextVisibility());
    ((NonScrollListView)view.findViewById(R.id.response_header_list)).setAdapter(HttpHeaderAdapter.newInstance(httpCallViewModel.getResponseHeaders()));
    ((NonScrollListView)view.findViewById(R.id.request_header_list)).setAdapter(HttpHeaderAdapter.newInstance(httpCallViewModel.getRequestHeaders()));
    view.findViewById(R.id.response_header_container).setVisibility(httpCallViewModel.getResponseHeaderVisibility());
    view.findViewById(R.id.request_header_container).setVisibility(httpCallViewModel.getRequestHeaderVisibility());
    return view;
  }
}
