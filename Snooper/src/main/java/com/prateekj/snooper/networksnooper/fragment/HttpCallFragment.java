package com.prateekj.snooper.networksnooper.fragment;

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
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.Bound;
import com.prateekj.snooper.networksnooper.presenter.HttpCallFragmentPresenter;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;

import java.util.List;

import static android.view.View.GONE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_MODE;

public class HttpCallFragment extends Fragment implements HttpCallBodyView{

  private int mode;
  private HttpBodyViewModel viewModel;
  private HttpCallFragmentPresenter presenter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_response_body, container, false);
    viewModel = new HttpBodyViewModel();
    SnooperRepo repo = new SnooperRepo(getActivity());
    long httpCallId = getArguments().getLong(HTTP_CALL_ID);
    mode = getArguments().getInt(HTTP_CALL_MODE);
    BackgroundTaskExecutor taskExecutor = new BackgroundTaskExecutor(this.getActivity());
    presenter = new HttpCallFragmentPresenter(repo, httpCallId, this, new ResponseFormatterFactory(), taskExecutor);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    changeLoaderVisibility(View.VISIBLE);
    presenter.init(viewModel, mode);
  }

  @Override
  public void onFormattingDone() {
    ((TextView)getView().findViewById(R.id.payload_text)).setText(viewModel.getFormattedBody());
    changeLoaderVisibility(GONE);
  }

  private void changeLoaderVisibility(int visible) {
    getView().findViewById(R.id.embedded_loader).setVisibility(visible);
  }

  @Override
  public void highlightBounds(List<Bound> bounds) {

  }

  @Override
  public void removeOldHighlightedSpans() {

  }
}
