package com.prateekj.snooper.networksnooper.views;


import com.prateekj.snooper.networksnooper.model.Bound;

import java.util.List;

public interface HttpCallBodyView {
  void onFormattingDone();
  void highlightBounds(List<Bound> bounds);
  void removeOldHighlightedSpans();
}
