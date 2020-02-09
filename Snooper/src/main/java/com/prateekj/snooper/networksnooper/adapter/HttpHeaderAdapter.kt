package com.prateekj.snooper.networksnooper.adapter

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.prateekj.snooper.R
import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.viewmodel.HttpHeaderViewModel
import kotlinx.android.synthetic.main.header_list_item.view.*

class HttpHeaderAdapter private constructor(headers: List<HttpHeader>) : BaseAdapter() {


  private val viewModels: List<HttpHeaderViewModel>

  init {
    viewModels = toViewModels(headers)
  }

  override fun getCount(): Int {
    return viewModels.size
  }

  override fun getItem(position: Int): Any {
    return viewModels[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val viewModel = viewModels[position]
    val view = convertView ?: newView(parent)
    return bindView(view, viewModel)
  }

  private fun newView(parent: ViewGroup): View {
    return from(parent.context).inflate(R.layout.header_list_item, parent, false)
  }

  private fun bindView(view: View, viewModel: HttpHeaderViewModel): View {
    view.header_name.text = viewModel.headerName()
    view.header_value.text = viewModel.headerValues()
    return view
  }


  private fun toViewModels(headers: List<HttpHeader>): List<HttpHeaderViewModel> {
    return headers.map { HttpHeaderViewModel(it) }
  }

  companion object {
    fun newInstance(headers: List<HttpHeader>): HttpHeaderAdapter {
      return HttpHeaderAdapter(headers)
    }
  }
}
