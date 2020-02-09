package com.prateekj.snooper.dbreader.activity

import android.content.Intent
import android.view.View
import androidx.test.espresso.intent.rule.IntentsTestRule

import com.prateekj.snooper.R
import com.prateekj.snooper.rules.TestDbRule

import org.junit.Rule
import org.junit.Test

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.prateekj.snooper.dbreader.activity.DatabaseListActivity.Companion.DB_NAME
import com.prateekj.snooper.dbreader.activity.DatabaseListActivity.Companion.DB_PATH
import com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView
import org.hamcrest.core.AllOf.allOf

class DatabaseListActivityTest {

  @get:Rule
  var testDbRule = TestDbRule(R.raw.test, "test.db")

  @get:Rule
  var activityRule = IntentsTestRule(DatabaseListActivity::class.java, true, false)

  @Test
  @Throws(Exception::class)
  fun shouldRenderListOfDatabases() {
    activityRule.launchActivity(null)

    val dbName = "test.db"
    val dbLocation = testDbRule.dbDirectory + "/" + dbName
    onView(withRecyclerView(R.id.db_list, 0)).check(
      matches(
        allOf<View>(
          hasDescendant(withText(dbName)),
          hasDescendant(withText(dbLocation))
        )
      )
    )

    onView(withText(dbName)).perform(click())
    intended(
      allOf<Intent>(
        hasComponent(DatabaseDetailActivity::class.java.name),
        hasExtra(DB_NAME, dbName),
        hasExtra(DB_PATH, dbLocation)
      )
    )
  }
}