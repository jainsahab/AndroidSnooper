package com.prateekj.snooper.networksnooper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.presenter.HttpCallFragmentPresenter;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;
import com.prateekj.snooper.networksnooper.views.HttpCallView;
import com.prateekj.snooper.realm.RealmFactory;

import io.realm.Realm;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_MODE;

public class HttpCallFragment extends Fragment implements HttpCallBodyView{

  private HttpCallView httpCallView;
  private int mode;
  private HttpBodyViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_response_body, container, false);
    Realm realm = RealmFactory.create(getActivity());
    viewModel = new HttpBodyViewModel();
    SnooperRepo repo = new SnooperRepo(realm);
    int httpCallId = getArguments().getInt(HTTP_CALL_ID);
    mode = getArguments().getInt(HTTP_CALL_MODE);
    BackgroundTaskExecutor taskExecutor = new BackgroundTaskExecutor(this.getActivity());
    HttpCallFragmentPresenter presenter = new HttpCallFragmentPresenter(repo, httpCallId, this, new ResponseFormatterFactory(), taskExecutor);
    presenter.init(viewModel, mode);
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    httpCallView = (HttpCallView) this.getActivity();
  }

  @Override
  public void onFormattingDone() {
    httpCallView.onHttpCallBodyFormatted(this.mode);
    ((TextView)getView().findViewById(R.id.payload_text)).setText(viewModel.getFormattedBody());
  }
}
