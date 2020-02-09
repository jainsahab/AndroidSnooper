package com.prateekj.snooper.networksnooper.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import com.prateekj.snooper.networksnooper.adapter.HttpHeaderAdapter
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel
import kotlinx.android.synthetic.main.fragment_headers.view.*
import kotlinx.android.synthetic.main.http_call_general_detail.view.*

class HttpHeadersFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    val view = inflater.inflate(R.layout.fragment_headers, container, false)
    val snooperRepo = SnooperRepo(activity!!)
    val httpCallId = arguments!!.getLong(HTTP_CALL_ID)
    val httpCallRecord = snooperRepo.findById(httpCallId)
    val httpCallViewModel = HttpCallViewModel(httpCallRecord)
    view.url.text = httpCallViewModel.url
    view.method.text = httpCallViewModel.method
    view.status_code.text = httpCallViewModel.statusCode
    view.status_text.text = httpCallViewModel.statusText
    view.time_stamp.text = httpCallViewModel.timeStamp
    view.response_info_container.visibility = httpCallViewModel.responseInfoVisibility
    view.error_text.visibility = httpCallViewModel.failedTextVisibility
    view.response_header_list.adapter =
      HttpHeaderAdapter.newInstance(httpCallViewModel.responseHeaders)
    view.request_header_list.adapter =
      HttpHeaderAdapter.newInstance(httpCallViewModel.requestHeaders)
    view.response_header_container.visibility = httpCallViewModel.responseHeaderVisibility
    view.request_header_container.visibility = httpCallViewModel.requestHeaderVisibility
    return view
  }
}
