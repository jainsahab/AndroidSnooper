package com.prateekj.snooper.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {

  private static final String TAG = FileUtil.class.getSimpleName();
  private static final String LOG_FILE_NAME = "/Log.txt";

  public String createLogFile(StringBuilder content) {
    FileOutputStream fileOutputStream = null;
    String filePath = "";
    try {
      File file = new File(Environment.getExternalStorageDirectory() + LOG_FILE_NAME);
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      fileOutputStream = new FileOutputStream(file);
      OutputStreamWriter myOutWriter = new OutputStreamWriter(fileOutputStream);
      myOutWriter.append(content.toString());
      myOutWriter.close();
      fileOutputStream.close();
      filePath = file.getAbsolutePath();
    } catch (FileNotFoundException e) {
      Logger.e(TAG, "File Not Found Exception occurred while closing the output stream", e);
    } catch (IOException e) {
      Logger.e(TAG, "IO Exception occurred while closing the output stream", e);
    } finally {
      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        Logger.e(TAG, "IO Exception occurred while closing the output stream", e);
      }
    }
    return filePath;
  }
}
