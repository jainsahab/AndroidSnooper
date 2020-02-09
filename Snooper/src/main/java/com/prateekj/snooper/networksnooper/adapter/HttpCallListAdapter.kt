package com.prateekj.snooper.networksnooper.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.viewmodel.HttpCallViewModel
import kotlinx.android.synthetic.main.activity_http_call_list_item.view.*

class HttpCallListAdapter(
  private var httpCallRecords: MutableList<HttpCallRecord>,
  private val listener: HttpCallListClickListener
) : RecyclerView.Adapter<HttpCallListAdapter.HttpCallViewHolder>() {

  class HttpCallViewHolder(
    private val view: View,
    private val listener: HttpCallListClickListener
  ) : RecyclerView.ViewHolder(view) {

    @SuppressLint("ResourceAsColor")
    fun bind(httpCall: HttpCallRecord) {
      val httpCallViewModel = HttpCallViewModel(httpCall)
      view.url.text = httpCallViewModel.url
      view.method.text = httpCallViewModel.method
      view.status_code.text = httpCallViewModel.statusCode
      view.status_text.text = httpCallViewModel.statusText
      view.time_stamp.text = httpCallViewModel.timeStamp
      view.response_info_container.visibility = httpCallViewModel.responseInfoVisibility
      view.error_text.visibility = httpCallViewModel.failedTextVisibility

      view.method.setTextColor(getColor(view.context, httpCallViewModel.getStatusColor()))
      view.status_code.setTextColor(getColor(view.context, httpCallViewModel.getStatusColor()))
      view.status_text.setTextColor(getColor(view.context, httpCallViewModel.getStatusColor()))
      setClickListener(httpCall)
    }

    private fun setClickListener(httpCall: HttpCallRecord) {
      this.itemView.setOnClickListener { listener.onClick(httpCall) }
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HttpCallViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val listItemView = inflater.inflate(R.layout.activity_http_call_list_item, parent, false)
    return HttpCallViewHolder(listItemView, listener)
  }

  override fun onBindViewHolder(holder: HttpCallViewHolder, position: Int) {
    holder.bind(httpCallRecords[position])
  }

  override fun getItemCount(): Int {
    return httpCallRecords.size
  }

  fun refreshData(httpCallRecords: MutableList<HttpCallRecord>) {
    this.httpCallRecords = httpCallRecords
  }

  fun appendData(httpCallRecords: List<HttpCallRecord>) {
    this.httpCallRecords.addAll(httpCallRecords)
  }
}
