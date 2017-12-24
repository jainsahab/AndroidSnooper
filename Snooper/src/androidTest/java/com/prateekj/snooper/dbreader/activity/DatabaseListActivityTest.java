package com.prateekj.snooper.dbreader.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.rules.TestDbRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.dbreader.activity.DatabaseListActivity.DB_NAME;
import static com.prateekj.snooper.dbreader.activity.DatabaseListActivity.DB_PATH;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static org.hamcrest.core.AllOf.allOf;

public class DatabaseListActivityTest {

  @Rule
  public TestDbRule testDbRule = new TestDbRule(R.raw.test, "test.db");

  @Rule
  public IntentsTestRule<DatabaseListActivity> activityRule = new IntentsTestRule<>(DatabaseListActivity.class, true, false);

  @Test
  public void shouldRenderListOfDatabases() throws Exception {
    activityRule.launchActivity(null);

    String dbName = "test.db";
    String dbLocation = testDbRule.getDBDirectory() + "/" + dbName;
    onView(withRecyclerView(R.id.db_list, 0)).check(matches(allOf(
      hasDescendant(withText(dbName)),
      hasDescendant(withText(dbLocation))
    )));

    onView(withText(dbName)).perform(click());
    intended(allOf(
      hasComponent(DatabaseDetailActivity.class.getName()),
      hasExtra(DB_NAME, dbName),
      hasExtra(DB_PATH, dbLocation)
    ));
  }
}