package com.prateekj.snooper.rules;


import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.test.InstrumentationRegistry;

import com.prateekj.snooper.utils.Logger;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestDbRule implements TestRule {
  private int dbRawResourceId;
  private String dbName;

  public TestDbRule(@RawRes int dbRawResourceId, String dbName) {
    this.dbRawResourceId = dbRawResourceId;
    this.dbName = dbName;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        copyDataBase(TestDbRule.this.dbName);
        base.evaluate();
        InstrumentationRegistry.getTargetContext().deleteDatabase(dbName);
      }
    };
  }

  private void copyDataBase(String finalDbName) {
    Context applicationContext = InstrumentationRegistry.getTargetContext();
    ContextWrapper cw = new ContextWrapper(applicationContext);
    File dbDir = new File(getDBDirectory());
    if(!dbDir.exists()) {
      dbDir.mkdir();
    }
    Logger.d("Database", "New database is being copied to device!");
    byte[] buffer = new byte[1024];
    int length;
    try {
      InputStream myInput = applicationContext.getResources().openRawResource(this.dbRawResourceId);
      File file = new File(dbDir , finalDbName);
      OutputStream myOutput = new FileOutputStream(file);
      while ((length = myInput.read(buffer)) > 0) {
        myOutput.write(buffer, 0, length);
      }
      myOutput.close();
      myOutput.flush();
      myInput.close();
      Logger.d("Database", "New database has been copied to device!");
    } catch (IOException e) {
      Logger.e("Database", e.getMessage(), e);
    }
  }

  @NonNull
  public String getDBDirectory() {
    ContextWrapper cw = new ContextWrapper(InstrumentationRegistry.getTargetContext());
    String destPath = cw.getFilesDir().getPath();
    return destPath.substring(0, destPath.lastIndexOf("/")) + "/databases";
  }
}
