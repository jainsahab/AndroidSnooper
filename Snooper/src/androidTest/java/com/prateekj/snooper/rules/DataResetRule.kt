package com.prateekj.snooper.rules


import androidx.test.InstrumentationRegistry.getTargetContext
import com.prateekj.snooper.database.SnooperDbHelper
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_TABLE_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HEADER_VALUE_TABLE_NAME
import com.prateekj.snooper.networksnooper.database.HttpCallRecordContract.Companion.HTTP_CALL_RECORD_TABLE_NAME
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DataResetRule : TestRule {

  private val snooperDbHelper: SnooperDbHelper = SnooperDbHelper.getInstance(getTargetContext())

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        try {
          base.evaluate()
        } finally {
          val database = snooperDbHelper.writableDatabase
          val tableToDelete = listOf(
            HEADER_VALUE_TABLE_NAME,
            HEADER_TABLE_NAME,
            HTTP_CALL_RECORD_TABLE_NAME
          )
          for (table in tableToDelete) {
            database.delete(table, null, null)
          }
          database.close()
        }
      }
    }
  }
}
