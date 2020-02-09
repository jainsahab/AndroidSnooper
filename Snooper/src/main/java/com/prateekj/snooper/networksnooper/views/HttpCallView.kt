package com.prateekj.snooper.networksnooper.views


import androidx.fragment.app.Fragment

interface HttpCallView {
  fun getRequestBodyFragment(): Fragment
  fun getResponseBodyFragment(): Fragment
  fun getHeadersFragment(): Fragment
  fun getExceptionFragment(): Fragment
  fun copyToClipboard(data: String)
  fun shareData(completeHttpCallData: String)
  fun showMessageShareNotAvailable()
}
