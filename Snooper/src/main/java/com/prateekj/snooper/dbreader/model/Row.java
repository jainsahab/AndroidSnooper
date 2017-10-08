package com.prateekj.snooper.dbreader.model;

import java.util.List;

public class Row {
  private List<String> data;

  public Row(List<String> data) {
    this.data = data;
  }

  public List<String> getData() {
    return data;
  }
}
