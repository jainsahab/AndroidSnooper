package com.prateekj.snooper.utils

import android.os.Environment

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class FileUtil {

  fun createLogFile(content: StringBuilder, fileName: String): String {
    var filePath = ""
    try {
      val file = File(Environment.getExternalStorageDirectory().toString() + "/" + fileName)
      if (file.exists()) {
        file.delete()
      }
      file.createNewFile()

      FileOutputStream(file).use { fos ->
        OutputStreamWriter(fos).use { osw ->
          osw.append(content.toString())
        }
      }
      filePath = file.absolutePath
    } catch (e: FileNotFoundException) {
      Logger.e(TAG, "File Not Found Exception occurred while closing the output stream", e)
    } catch (e: IOException) {
      Logger.e(TAG, "IO Exception occurred while closing the output stream", e)
    }
    return filePath
  }

  companion object {
    private val TAG = FileUtil::class.java.simpleName
  }
}
