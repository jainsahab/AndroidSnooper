package com.prateekj.snooper.dbreader.activity;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.prateekj.snooper.R;
import com.prateekj.snooper.rules.TestDbRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.prateekj.snooper.utils.EspressoViewMatchers.withTableLayout;

public class TableDetailActivityTest {
  @Rule
  public TestDbRule testDbRule = new TestDbRule(R.raw.test, "test.db");

  @Rule
  public IntentsTestRule<TableDetailActivity> activityRule = new IntentsTestRule<>(TableDetailActivity.class, true, false);


  @Test
  public void shouldRenderDataOfTable() throws Exception {
    Intent intent = new Intent();
    intent.putExtra(DatabaseDetailActivity.TABLE_NAME, "person");
    intent.putExtra(DatabaseDetailActivity.DB_PATH, testDbRule.getDBDirectory() + "/test.db");
    activityRule.launchActivity(intent);
    onView(withTableLayout(R.id.table, 0, 0)).check(matches(withText("S.No.")));
    onView(withTableLayout(R.id.table, 0, 1)).check(matches(withText("_ID")));
    onView(withTableLayout(R.id.table, 0, 2)).check(matches(withText("NAME")));
    onView(withTableLayout(R.id.table, 1, 0)).check(matches(withText("1")));
    onView(withTableLayout(R.id.table, 1, 1)).check(matches(withText("1")));
    onView(withTableLayout(R.id.table, 1, 2)).check(matches(withText("Mr. Smith")));
    onView(withTableLayout(R.id.table, 2, 1)).check(matches(withText("2")));
    onView(withTableLayout(R.id.table, 2, 2)).check(matches(withText("Mr. Randolf")));
  }


}