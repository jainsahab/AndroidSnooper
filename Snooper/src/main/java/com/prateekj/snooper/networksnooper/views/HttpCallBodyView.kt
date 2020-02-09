package com.prateekj.snooper.networksnooper.views


import com.prateekj.snooper.networksnooper.model.Bound

interface HttpCallBodyView {
  fun onFormattingDone()
  fun highlightBounds(bounds: List<Bound>)
  fun removeOldHighlightedSpans()
}
