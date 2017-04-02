package com.prateekj.snooper.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateekj.snooper.R;
import com.prateekj.snooper.customviews.DividerItemDecoration;
import com.prateekj.snooper.databinding.HttpHeaderBinding;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;

import io.realm.Realm;

import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class HttpHeadersFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    HttpHeaderBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headers, container, false);
    Realm realm = RealmFactory.create(getActivity());
    SnooperRepo snooperRepo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    binding.setHttpCall(snooperRepo.findById(httpCallId));
    binding.setDivider(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL, R.drawable.grey_divider));
    return binding.getRoot();
  }
}
