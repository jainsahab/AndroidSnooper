package com.prateekj.snooper.dbreader.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.rules.TestDbRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static org.hamcrest.core.AllOf.allOf;

public class DbViewActivityTest {
  @Rule
  public TestDbRule testDbRule = new TestDbRule(R.raw.test, "test.db");

  @Rule
  public IntentsTestRule<DbViewActivity> activityRule = new IntentsTestRule<>(DbViewActivity.class, true, false);


  @Test
  public void shouldRenderDatabaseInformation() throws Exception {
    Intent intent = new Intent();
    intent.putExtra(DbViewActivity.DB_PATH, testDbRule.getDBDirectory() + "/test.db");
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
  }
}