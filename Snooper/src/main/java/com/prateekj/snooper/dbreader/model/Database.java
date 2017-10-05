package com.prateekj.snooper.dbreader.model;

import static android.R.attr.version;

public class Database {
  private String name;
  private String path;

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public int getVersion() {
    return version;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
