package com.prateekj.snooper.viewmodel;

import android.databinding.BaseObservable;

public class HttpBodyViewModel extends BaseObservable {

  private String formattedBody;

  public void init(String formattedBody) {
    this.formattedBody = formattedBody;
  }

  public String getFormattedBody() {
    return this.formattedBody;
  }
}
