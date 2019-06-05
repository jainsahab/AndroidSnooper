package com.prateekj.snooper.rules;


import android.database.sqlite.SQLiteDatabase;

import com.prateekj.snooper.database.SnooperDbHelper;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Arrays;
import java.util.List;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_TABLE_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HEADER_VALUE_TABLE_NAME;
import static com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.HTTP_CALL_RECORD_TABLE_NAME;

public class DataResetRule implements TestRule {

  private final SnooperDbHelper snooperDbHelper;

  public DataResetRule() {
    snooperDbHelper = SnooperDbHelper.getInstance(getTargetContext());
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        } finally {
          SQLiteDatabase database = snooperDbHelper.getWritableDatabase();
          List<String> tableToDelete = Arrays.asList(HEADER_VALUE_TABLE_NAME, HEADER_TABLE_NAME, HTTP_CALL_RECORD_TABLE_NAME);
          for (String table : tableToDelete) {
            database.delete(table, null, null);
          }
          database.close();
        }
      }
    };
  }
}
