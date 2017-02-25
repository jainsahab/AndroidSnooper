package com.prateekj.snooper.model;

import org.junit.Before;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IdInitializerTest {

  private Realm realm;

  @Before
  public void setUp() throws Exception {
    Realm.init(getTargetContext());
    RealmConfiguration configuration = new RealmConfiguration.Builder()
        .name("test.realm")
        .modules(new RealmTestModule())
        .build();
    realm = Realm.getInstance(configuration);
    cleanDB();
  }

  @Test
  public void shouldInitializeFirstItemWithId_1() throws Exception {
    TestModel model = new TestModel();
    new IdInitializer(realm).initialize(model);
    saveModel(model);
    TestModel persistedModel = realm.where(TestModel.class).findFirst();
    assertThat(persistedModel.id, is(1));
  }

  @Test
  public void shouldInitializeModelsWithIncrementalId() throws Exception {
    TestModel firstModel = new TestModel();
    firstModel.id = 1;
    saveModel(firstModel);
    TestModel secondModel = new TestModel();
    new IdInitializer(realm).initialize(secondModel);
    saveModel(secondModel);
    TestModel persistedModel = realm.where(TestModel.class).equalTo("id", 2).findFirst();
    assertThat(persistedModel.id, is(2));
  }

  private void cleanDB() {
    realm.beginTransaction();
    realm.deleteAll();
    realm.commitTransaction();
  }

  private void saveModel(RealmObject object) {
    realm.beginTransaction();
    realm.copyToRealm(object);
    realm.commitTransaction();
  }
}