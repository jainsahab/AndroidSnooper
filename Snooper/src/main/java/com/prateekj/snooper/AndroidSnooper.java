package com.prateekj.snooper;

import android.content.Context;

import com.prateekj.snooper.model.HttpCall;
import com.prateekj.snooper.repo.SnooperRepo;

import java.io.IOException;

import io.realm.Realm;

public class AndroidSnooper {

  private SnooperRepo snooperRepo;

  private AndroidSnooper(){}

  public void record(HttpCall httpCall) throws IOException {
    this.snooperRepo.save(httpCall);
  }

  public static AndroidSnooper init(Context context) {
    Realm.init(context);
    SnooperRepo repo = new SnooperRepo(Realm.getDefaultInstance());
    AndroidSnooper androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = repo;
    return androidSnooper;
  }
}
