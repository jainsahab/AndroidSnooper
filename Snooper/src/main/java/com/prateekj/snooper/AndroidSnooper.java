package com.prateekj.snooper;

import android.content.Context;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.realm.RealmFactory;
import com.prateekj.snooper.repo.SnooperRepo;

import java.io.IOException;

public class AndroidSnooper {

  private SnooperRepo snooperRepo;

  private AndroidSnooper(){}

  public void record(HttpCall httpCall) throws IOException {
    this.snooperRepo.save(httpCall);
  }

  public static AndroidSnooper init(Context context) {
    SnooperRepo repo = new SnooperRepo(RealmFactory.create(context));
    AndroidSnooper androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = repo;
    return androidSnooper;
  }
}
