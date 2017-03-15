package com.prateekj.snooper.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.HttpBodyBinding;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.presenter.HttpCallFragmentPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.views.HttpCallBodyView;
import com.prateekj.snooper.views.HttpCallView;

import io.realm.Realm;

import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.activity.HttpCallActivity.HTTP_CALL_MODE;

public class HttpCallFragment extends Fragment implements HttpCallBodyView{

  private HttpCallView httpCallView;
  private int mode;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    final HttpBodyBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_response_body, container, false);
    Realm realm = RealmFactory.create(getActivity());
    HttpBodyViewModel viewModel = new HttpBodyViewModel();
    SnooperRepo repo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    mode = getArguments().getInt(HTTP_CALL_MODE);
    BackgroundTaskExecutor taskExecutor = new BackgroundTaskExecutor(this.getActivity());
    HttpCallFragmentPresenter presenter = new HttpCallFragmentPresenter(repo, httpCallId, this, new ResponseFormatterFactory(), taskExecutor);
    presenter.init(viewModel, mode);
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    httpCallView = (HttpCallView) this.getActivity();
  }

  @Override
  public void onFormattingDone() {
    httpCallView.onHttpCallBodyFormatted(this.mode);
  }
}
