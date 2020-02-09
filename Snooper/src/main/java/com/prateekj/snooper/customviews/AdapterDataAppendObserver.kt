package com.prateekj.snooper.customviews

import androidx.recyclerview.widget.RecyclerView

class AdapterDataAppendObserver(private val listener: PageAddedListener) :
  RecyclerView.AdapterDataObserver() {

  override fun onChanged() {
    super.onChanged()
    listener.onPageAdded()
  }
}
