package com.prateekj.snooper.networksnooper.viewmodel;

public class HttpBodyViewModel {

  private String formattedBody;

  public void init(String formattedBody) {
    setFormattedBody(formattedBody);
  }

  public String getFormattedBody() {
    return this.formattedBody;
  }

  public void setFormattedBody(String formattedBody) {
    this.formattedBody = formattedBody;
  }
}