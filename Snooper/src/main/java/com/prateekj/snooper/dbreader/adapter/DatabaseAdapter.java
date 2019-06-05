package com.prateekj.snooper.dbreader.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.model.Database;

import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.DbViewHolder> {

  private List<Database> databaseList;
  private DbEventListener dbEventListener;

  class DbViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView path;

    DbViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.name);
      path = (TextView) view.findViewById(R.id.path);
    }

    void bind(final Database db) {
      this.name.setText(db.getName());
      this.path.setText(db.getPath());
      this.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dbEventListener.onDatabaseClick(db);
        }
      });
    }
  }

  public DatabaseAdapter(List<Database> databaseList, DbEventListener dbEventListener) {
    this.databaseList = databaseList;
    this.dbEventListener = dbEventListener;
  }

  @Override
  public DbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.db_card_item, parent, false);
    return new DbViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final DbViewHolder holder, int position) {
    holder.bind(databaseList.get(position));
  }

  @Override
  public int getItemCount() {
    return databaseList.size();
  }
}