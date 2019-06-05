package com.prateekj.snooper.dbreader.activity;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.rules.TestDbRule;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.dbreader.activity.DatabaseDetailActivity.TABLE_NAME;
import static com.prateekj.snooper.dbreader.activity.DatabaseListActivity.DB_PATH;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static org.hamcrest.core.AllOf.allOf;

public class DatabaseDetailActivityTest {
  @Rule
  public TestDbRule testDbRule = new TestDbRule(R.raw.test, "test.db");

  @Rule
  public IntentsTestRule<DatabaseDetailActivity> activityRule = new IntentsTestRule<>(DatabaseDetailActivity.class, true, false);


  @Test
  public void shouldRenderDatabaseInformation() throws Exception {
    Intent intent = new Intent();
    String dbPath = testDbRule.getDBDirectory() + "/test.db";
    intent.putExtra(DatabaseDetailActivity.DB_PATH, dbPath);
    intent.putExtra(DatabaseListActivity.DB_NAME, "test.db");
    activityRule.launchActivity(intent);
    onView(withId(R.id.db_name)).check(matches(withText("test.db")));
    onView(withId(R.id.db_version)).check(matches(withText("0")));
    onView(withRecyclerView(R.id.table_list, 0)).check(matches(allOf(
      hasDescendant(withText("1. ")),
      hasDescendant(withText("person"))
    )));
    onView(withRecyclerView(R.id.table_list, 1)).check(matches(allOf(
      hasDescendant(withText("2. ")),
      hasDescendant(withText("sqlite_sequence"))
    )));
    onView(withRecyclerView(R.id.table_list, 0)).perform(click());
    intended(CoreMatchers.allOf(
      hasComponent(TableDetailActivity.class.getName()),
      hasExtra(TABLE_NAME, "person"),
      hasExtra(DB_PATH, dbPath)
    ));
  }
}