package com.prateekj.snooper.networksnooper.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.HttpHeaderBinding;
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
    HttpHeaderBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headers, container, false);
    Realm realm = RealmFactory.create(getActivity());
    SnooperRepo snooperRepo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    HttpCall httpCall = snooperRepo.findById(httpCallId);
    binding.setHttpCallViewModel(new HttpCallViewModel(httpCall));
    return binding.getRoot();
  }
}
