package com.prateekj.snooper.rules


import android.content.ContextWrapper
import androidx.annotation.RawRes
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.prateekj.snooper.utils.Logger
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TestDbRule(@param:RawRes private val dbRawResourceId: Int, private val dbName: String) :
  TestRule {

  val dbDirectory: String
    get() {
      val cw = ContextWrapper(getInstrumentation().targetContext)
      val destPath = cw.filesDir.path
      return destPath.substring(0, destPath.lastIndexOf("/")) + "/databases"
    }

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        copyDataBase(this@TestDbRule.dbName)
        base.evaluate()
        getInstrumentation().targetContext.deleteDatabase(dbName)
      }
    }
  }

  private fun copyDataBase(finalDbName: String) {
    val applicationContext = getInstrumentation().targetContext
    val dbDir = File(dbDirectory)
    if (!dbDir.exists()) {
      dbDir.mkdir()
    }
    Logger.d("Database", "New database is being copied to device!")
    try {
      val myInput = applicationContext.resources.openRawResource(this.dbRawResourceId)
      val file = File(dbDir, finalDbName)
      val myOutput = FileOutputStream(file)

      myInput.use { inputStream ->
        myOutput.use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      Logger.d("Database", "New database has been copied to device!")
    } catch (e: IOException) {
      Logger.e("Database", e.message, e)
    }
  }
}
