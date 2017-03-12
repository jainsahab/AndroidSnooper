package com.prateekj.snooper;

import android.databinding.BaseObservable;
import android.databinding.Observable;

import java.util.HashMap;

public class ObservableViewModelTest {
  private HashMap<Integer, Integer> notifiedProperties;

  public ObservableViewModelTest() {
    notifiedProperties = new HashMap<>();
  }

  protected void setObservable(BaseObservable baseObservable) {
    baseObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(Observable observable, int propertyId) {
        Integer currentCount = notifiedProperties.get(propertyId);
        int count = currentCount == null ? 0 : currentCount;
        notifiedProperties.put(propertyId, count);
      }
    });
  }

  public boolean isPropertyNotified(int propertyId) {
    return notifiedProperties.get(propertyId) != null;
  }
}