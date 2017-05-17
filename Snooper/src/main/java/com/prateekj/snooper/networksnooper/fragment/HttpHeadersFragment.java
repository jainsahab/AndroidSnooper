package com.prateekj.snooper.networksnooper.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.customviews.NonScrollListView;
import com.prateekj.snooper.networksnooper.adapter.HttpHeaderAdapter;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel;
import com.prateekj.snooper.realm.RealmFactory;

import io.realm.Realm;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class HttpHeadersFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_headers, container, false);
    Realm realm = RealmFactory.create(getActivity());
    SnooperRepo snooperRepo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    HttpCall httpCall = snooperRepo.findById(httpCallId);
    HttpCallViewModel httpCallViewModel = new HttpCallViewModel(httpCall);
    ((TextView)view.findViewById(R.id.url)).setText(httpCallViewModel.getUrl());
    ((TextView)view.findViewById(R.id.method)).setText(httpCallViewModel.getMethod());
    ((TextView)view.findViewById(R.id.status_code)).setText(httpCallViewModel.getStatusCode());
    ((TextView)view.findViewById(R.id.status_text)).setText(httpCallViewModel.getStatusText());
    ((TextView)view.findViewById(R.id.time_stamp)).setText(httpCallViewModel.getTimeStamp());
    ((NonScrollListView)view.findViewById(R.id.response_header_list)).setAdapter(HttpHeaderAdapter.newInstance(httpCallViewModel.getResponseHeaders()));
    ((NonScrollListView)view.findViewById(R.id.request_header_list)).setAdapter(HttpHeaderAdapter.newInstance(httpCallViewModel.getRequestHeaders()));
    return view;
  }
}
