package com.prateekj.snooper.dbreader.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.dbreader.DatabaseReader;
import com.prateekj.snooper.dbreader.DatabaseDataReader;
import com.prateekj.snooper.dbreader.model.Row;
import com.prateekj.snooper.dbreader.model.Table;
import com.prateekj.snooper.dbreader.view.TableViewCallback;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.activity.SnooperBaseActivity;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.prateekj.snooper.dbreader.activity.DatabaseDetailActivity.TABLE_NAME;

public class TableDetailActivity extends SnooperBaseActivity implements TableViewCallback {
  private View embeddedLoader;
  private DatabaseReader databaseReader;
  private TableLayout tableLayout;
  private String tableName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_table_view);
    initViews();
    tableName = getIntent().getStringExtra(TABLE_NAME);
    String dbPath = getIntent().getStringExtra(DatabaseDetailActivity.DB_PATH);
    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this);
    embeddedLoader = findViewById(R.id.embedded_loader);
    databaseReader = new DatabaseReader(this, backgroundTaskExecutor, new DatabaseDataReader());
    databaseReader.fetchTableContent(this, dbPath, tableName);
  }

  @Override
  public void onTableFetchStarted() {
    embeddedLoader.setVisibility(VISIBLE);
  }

  @Override
  public void onTableFetchCompleted(Table table) {
    embeddedLoader.setVisibility(GONE);
    updateView(table);
  }

  private void initViews() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

  }

  private void updateView(Table table) {
    tableLayout = (TableLayout) findViewById(R.id.table);
    addTableColumnNames(table);
    addTableRowsToUi(table);
  }

  private void addTableRowsToUi(Table table) {
    List<Row> rows = table.getRows();
    for (int i = 0; i < rows.size(); i++) {
      tableLayout.addView(addRowData(rows.get(i).getData(), i + 1));
    }
  }

  private void addTableColumnNames(Table table) {
    TableRow columnRow = new TableRow(this);
    TextView serialNoCell = getCellView(getString(R.string.serial_number_column_heading));
    serialNoCell.setTypeface(null, Typeface.BOLD);
    columnRow.addView(serialNoCell);
    for (String column : table.getColumns()) {
      TextView columnView = getCellView(column);
      columnView.setBackgroundColor(ContextCompat.getColor(this, R.color.snooper_grey));
      columnView.setBackground(ContextCompat.getDrawable(this, R.drawable.table_cell_background));
      columnView.setTypeface(null, Typeface.BOLD);
      columnRow.addView(columnView);
    }
    tableLayout.addView(columnRow);
  }

  private TableRow addRowData(List<String> data, int serialNumber) {
    TableRow row = new TableRow(this);
    row.addView(getCellView(Integer.toString(serialNumber)));
    for (String cellValue : data) {
      row.addView(getCellView(cellValue));
    }
    return row;
  }

  @NonNull
  private TextView getCellView(String cellValue) {
    TextView textView = new TextView(this);
    textView.setPadding(1, 0, 0, 0);
    textView.setBackground(ContextCompat.getDrawable(this, R.drawable.table_cell_background));
    textView.setText(cellValue);
    return textView;
  }
}