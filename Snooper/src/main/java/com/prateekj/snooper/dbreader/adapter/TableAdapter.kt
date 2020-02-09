package com.prateekj.snooper.dbreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prateekj.snooper.R
import kotlinx.android.synthetic.main.table_item.view.*

class TableAdapter(
  private val tableList: List<String>,
  private val tableEventListener: TableEventListener
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

  class TableViewHolder(
    val view: View,
    private val tableEventListener: TableEventListener
  ) : RecyclerView.ViewHolder(view) {

    fun bind(tableName: String, rowNum: Int) {
      view.table_name.text = tableName
      view.row_num.text = "$rowNum. "
      this.itemView.setOnClickListener { tableEventListener.onTableClick(tableName) }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.table_item, parent, false)
    return TableViewHolder(itemView, tableEventListener)
  }

  override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
    holder.bind(tableList[position], position + 1)
  }

  override fun getItemCount(): Int {
    return tableList.size
  }
}