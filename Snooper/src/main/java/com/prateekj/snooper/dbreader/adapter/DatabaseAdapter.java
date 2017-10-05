package com.prateekj.snooper.dbreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.model.Database;

import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.DbViewHolder> {

  private List<Database> databaseList;

  public class DbViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView path;

    public DbViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.name);
      path = (TextView) view.findViewById(R.id.path);
    }
  }

  public DatabaseAdapter(List<Database> databaseList) {
    this.databaseList = databaseList;
  }

  @Override
  public DbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.db_card_item, parent, false);

    return new DbViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final DbViewHolder holder, int position) {
    Database db = databaseList.get(position);
    holder.name.setText(db.getName());
    holder.path.setText(db.getPath());
  }

  @Override
  public int getItemCount() {
    return databaseList.size();
  }
}
