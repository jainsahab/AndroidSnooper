package com.prateekj.snooper.dbreader.activity

import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule

import com.prateekj.snooper.R
import com.prateekj.snooper.rules.TestDbRule

import org.junit.Rule
import org.junit.Test

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.prateekj.snooper.utils.EspressoViewMatchers.withTableLayout

class TableDetailActivityTest {
  @get:Rule
  var testDbRule = TestDbRule(R.raw.test, "test.db")

  @get:Rule
  var activityRule = IntentsTestRule(TableDetailActivity::class.java, true, false)

  @Test
  @Throws(Exception::class)
  fun shouldRenderDataOfTable() {
    val intent = Intent()
    intent.putExtra(DatabaseDetailActivity.TABLE_NAME, "person")
    intent.putExtra(DatabaseDetailActivity.DB_PATH, testDbRule.dbDirectory + "/test.db")
    activityRule.launchActivity(intent)
    onView(withTableLayout(R.id.table_layout, 0, 0)).check(matches(withText("S.No.")))
    onView(withTableLayout(R.id.table_layout, 0, 1)).check(matches(withText("_ID")))
    onView(withTableLayout(R.id.table_layout, 0, 2)).check(matches(withText("NAME")))
    onView(withTableLayout(R.id.table_layout, 1, 0)).check(matches(withText("1")))
    onView(withTableLayout(R.id.table_layout, 1, 1)).check(matches(withText("1")))
    onView(withTableLayout(R.id.table_layout, 1, 2)).check(matches(withText("Mr. Smith")))
    onView(withTableLayout(R.id.table_layout, 2, 1)).check(matches(withText("2")))
    onView(withTableLayout(R.id.table_layout, 2, 2)).check(matches(withText("Mr. Randolf")))
  }
}