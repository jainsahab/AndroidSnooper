package com.prateekj.snooper.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prateekj.snooper.R;
import com.prateekj.snooper.databinding.ResponseBodyBinding;
import com.prateekj.snooper.presenter.ResponseBodyPresenter;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.viewmodel.ResponseBodyViewModel;

import io.realm.Realm;

public class ResponseBodyActivity extends AppCompatActivity {

  public static String HTTP_CALL_ID = "HTTP_CALL_ID";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ResponseBodyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_response_body);
    Realm realm = RealmFactory.create(this);
    ResponseBodyViewModel viewModel = new ResponseBodyViewModel();
    SnooperRepo repo = new SnooperRepo(realm);
    int httpCallId = getIntent().getIntExtra(HTTP_CALL_ID, 0);
    ResponseBodyPresenter presenter = new ResponseBodyPresenter(repo, httpCallId);
    presenter.init(viewModel);
    binding.setViewModel(viewModel);
  }
}
