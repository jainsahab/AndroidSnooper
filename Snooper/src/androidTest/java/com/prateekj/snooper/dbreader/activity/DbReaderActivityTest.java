package com.prateekj.snooper.dbreader.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.rules.TestDbRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withRecyclerView;
import static org.hamcrest.core.AllOf.allOf;

public class DbReaderActivityTest {

  @Rule
  public TestDbRule testDbRule = new TestDbRule(R.raw.test, "test.db");

  @Rule
  public IntentsTestRule<DbReaderActivity> activityRule = new IntentsTestRule<>(DbReaderActivity.class, true, false);


  @Test
  public void shouldRenderListOfDatabases() throws Exception {
    activityRule.launchActivity(null);

    onView(withRecyclerView(R.id.db_list, 0)).check(matches(allOf(
      hasDescendant(withText("test.db")),
      hasDescendant(withText(testDbRule.getDBDirectory() + "/test.db"))
    )));
  }
}