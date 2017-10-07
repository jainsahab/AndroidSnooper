package com.prateekj.snooper.dbreader.model;

import java.util.List;

public class Table {
  private String name;
  private List<String> columns;
  private List<Row> rows;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getColumns() {
    return columns;
  }

  public void setColumns(List<String> columns) {
    this.columns = columns;
  }

  public List<Row> getRows() {
    return rows;
  }

  public void setRows(List<Row> rows) {
    this.rows = rows;
  }
}
