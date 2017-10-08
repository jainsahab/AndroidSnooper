package com.prateekj.snooper.dbreader.model;

import java.util.List;

public class Database {
  private String name;
  private String path;
  private int version;
  private List<String> tables;

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

  public void setVersion(int version) {
    this.version = version;
  }

  public void setTables(List<String> tables) {
    this.tables = tables;
  }

  public List<String> getTables() {
    return tables;
  }
}