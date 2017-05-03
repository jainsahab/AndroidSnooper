package com.prateekj.snooper.networksnooper.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestModel extends RealmObject implements IncrementalIdModel {
  @PrimaryKey
  public int id;

  @Override
  public Class getClazz() {
    return TestModel.class;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }
}