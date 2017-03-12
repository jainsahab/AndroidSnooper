package com.prateekj.snooper.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.prateekj.snooper.BR;

public class HttpBodyViewModel extends BaseObservable {

  private String formattedBody;

  public void init(String formattedBody) {
    setFormattedBody(formattedBody);
  }

  @Bindable
  public String getFormattedBody() {
    return this.formattedBody;
  }

  public void setFormattedBody(String formattedBody) {
    this.formattedBody = formattedBody;
    notifyPropertyChanged(BR.formattedBody);
  }
}