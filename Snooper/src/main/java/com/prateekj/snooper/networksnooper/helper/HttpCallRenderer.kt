package com.prateekj.snooper.networksnooper.helper


import androidx.fragment.app.Fragment
import com.prateekj.snooper.networksnooper.activity.HttpCallTab
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.ERROR
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE
import com.prateekj.snooper.networksnooper.views.HttpCallView

class HttpCallRenderer(private val httpCallView: HttpCallView, private val hasError: Boolean) {

  fun getTabs(): List<HttpCallTab> {
    return if (hasError) {
      listOf(ERROR, REQUEST, HEADERS)
    } else listOf(RESPONSE, REQUEST, HEADERS)
  }

  fun getFragment(position: Int): Fragment {
    if (position == 0 && this.hasError)
      return httpCallView.getExceptionFragment()
    if (position == 0)
      return httpCallView.getResponseBodyFragment()
    return if (position == 1) httpCallView.getRequestBodyFragment() else httpCallView.getHeadersFragment()
  }
}