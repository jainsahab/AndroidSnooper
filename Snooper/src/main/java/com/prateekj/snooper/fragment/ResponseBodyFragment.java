package com.prateekj.snooper.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.ResponseBodyBinding;
import com.prateekj.snooper.presenter.ResponseBodyPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.ResponseBodyViewModel;

import io.realm.Realm;

import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;

public class ResponseBodyFragment extends Fragment {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    ResponseBodyBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_response_body, container, false);
    Realm realm = RealmFactory.create(getActivity());
    ResponseBodyViewModel viewModel = new ResponseBodyViewModel();
    SnooperRepo repo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    ResponseBodyPresenter presenter = new ResponseBodyPresenter(repo, httpCallId);
    presenter.init(viewModel);
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }
}
