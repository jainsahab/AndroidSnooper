package com.prateekj.snooper.utils;


import java.util.List;

public class CollectionUtilities {
  public static <T>  T last(List<T> list) {
    return list.get(list.size() - 1);
  }
}
