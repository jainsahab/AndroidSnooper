package com.prateekj.snooper.dbreader.activity

import android.content.Intent
import android.view.View

import androidx.test.espresso.intent.rule.IntentsTestRule

import com.prateekj.snooper.R
import com.prateekj.snooper.rules.TestDbRule

import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.prateekj.snooper.dbreader.activity.DatabaseDetailActivity.Companion.TABLE_NAME
import com.prateekj.snooper.dbreader.activity.DatabaseListActivity.Companion.DB_PATH
import com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView
import org.hamcrest.core.AllOf.allOf

class DatabaseDetailActivityTest {
  @get:Rule
  var testDbRule = TestDbRule(R.raw.test, "test.db")

  @get:Rule
  var activityRule = IntentsTestRule(DatabaseDetailActivity::class.java, true, false)

  @Test
  @Throws(Exception::class)
  fun shouldRenderDatabaseInformation() {
    val intent = Intent()
    val dbPath = testDbRule.dbDirectory + "/test.db"
    intent.putExtra(DatabaseDetailActivity.DB_PATH, dbPath)
    intent.putExtra(DatabaseListActivity.DB_NAME, "test.db")
    activityRule.launchActivity(intent)
    onView(withId(R.id.db_name)).check(matches(withText("test.db")))
    onView(withId(R.id.db_version)).check(matches(withText("0")))
    onView(withRecyclerView(R.id.table_list, 0)).check(
      matches(
        allOf<View>(
          hasDescendant(withText("1. ")),
          hasDescendant(withText("person"))
        )
      )
    )
    onView(withRecyclerView(R.id.table_list, 1)).check(
      matches(
        allOf<View>(
          hasDescendant(withText("2. ")),
          hasDescendant(withText("sqlite_sequence"))
        )
      )
    )
    onView(withRecyclerView(R.id.table_list, 0)).perform(click())
    intended(
      CoreMatchers.allOf(
        hasComponent(TableDetailActivity::class.java.name),
        hasExtra(TABLE_NAME, "person"),
        hasExtra(DB_PATH, dbPath)
      )
    )
  }
}