package com.prateekj.snooper.dbreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prateekj.snooper.R
import com.prateekj.snooper.dbreader.model.Database
import kotlinx.android.synthetic.main.db_card_item.view.*

class DatabaseAdapter(
  private val databaseList: List<Database>,
  private val dbEventListener: DbEventListener
) : RecyclerView.Adapter<DatabaseAdapter.DbViewHolder>() {

  class DbViewHolder(
    val view: View,
    private val dbEventListener: DbEventListener
  ) : RecyclerView.ViewHolder(view) {

    fun bind(db: Database) {
      view.name.text = db.name
      view.path.text = db.path
      this.itemView.setOnClickListener { dbEventListener.onDatabaseClick(db) }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbViewHolder {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.db_card_item, parent, false)
    return DbViewHolder(itemView, dbEventListener)
  }

  override fun onBindViewHolder(holder: DbViewHolder, position: Int) {
    holder.bind(databaseList[position])
  }

  override fun getItemCount(): Int {
    return databaseList.size
  }
}