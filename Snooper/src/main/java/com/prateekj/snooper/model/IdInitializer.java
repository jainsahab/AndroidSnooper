package com.prateekj.snooper.model;

import io.realm.Realm;

public class IdInitializer {
  private Realm realm;

  public IdInitializer(Realm realm) {
    this.realm = realm;
  }

  public void initialize(IncrementalIdModel model) {
    if (this.realm.where(model.getClazz()).findAll().size() == 0) {
      model.setId(1);
      return;
    }
    int id = this.realm.where(model.getClazz()).max("id").intValue();
    model.setId(++id);
  }
}
