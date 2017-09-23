package com.prateekj.snooper.networksnooper.model;

public class Bound {
  private int left;
  private int right;

  public Bound(int left, int right) {
    this.left = left;
    this.right = right;
  }

  public int getLeft() {
    return left;
  }

  public int getRight() {
    return right;
  }
}
