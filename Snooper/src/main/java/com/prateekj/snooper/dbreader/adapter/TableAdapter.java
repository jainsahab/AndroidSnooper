package com.prateekj.snooper.dbreader.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

  private List<String> tableList;
  private TableEventListener tableEventListener;

  class TableViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView rowNum;

    TableViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.table_name);
      rowNum = (TextView) view.findViewById(R.id.row_num);
    }

    void bind(final String tableName, int rowNum) {
      this.name.setText(tableName);
      this.rowNum.setText(Integer.toString(rowNum) + ". ");
      this.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          tableEventListener.onTableClick(tableName);
        }
      });
    }
  }

  public TableAdapter(List<String> tableList, TableEventListener tableEventListener) {
    this.tableList = tableList;
    this.tableEventListener = tableEventListener;
  }

  @Override
  public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.table_item, parent, false);
    return new TableViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final TableViewHolder holder, int position) {
    holder.bind(tableList.get(position), position + 1);
  }

  @Override
  public int getItemCount() {
    return tableList.size();
  }
}