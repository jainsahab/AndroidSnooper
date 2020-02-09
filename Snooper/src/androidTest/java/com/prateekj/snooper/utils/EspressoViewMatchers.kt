package com.prateekj.snooper.utils

import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher

object EspressoViewMatchers {

  fun withRecyclerView(recyclerViewId: Int, position: Int): Matcher<View> {

    return object : CustomTypeSafeMatcher<View>(
      "recycler view with id: $recyclerViewId at position: $position"
    ) {
      override fun matchesSafely(item: View): Boolean {
        val view = item.rootView.findViewById<View>(recyclerViewId)

        if (view == null || view !is RecyclerView)
          return false
        val childView = view.findViewHolderForAdapterPosition(position)!!.itemView
        return childView === item
      }
    }
  }

  fun withTableLayout(tableLayoutId: Int, row: Int, column: Int): Matcher<View> {
    return object : CustomTypeSafeMatcher<View>(
      "Table layout with id: $tableLayoutId at row: $row and column: $column"
    ) {
      override fun matchesSafely(item: View): Boolean {
        val view = item.rootView.findViewById<View>(tableLayoutId)

        if (view == null || view !is TableLayout)
          return false
        val tableRow = view.getChildAt(row) as TableRow
        val childView = tableRow.getChildAt(column)
        return childView === item
      }
    }
  }

  fun withListSize(size: Int): Matcher<View> {
    return object : CustomTypeSafeMatcher<View>(
      "recycler view with id: $size "
    ) {
      override fun matchesSafely(view: View?): Boolean {

        if (view == null || view !is RecyclerView)
          return false
        val recyclerView = view as RecyclerView?
        return recyclerView!!.adapter!!.itemCount == size
      }
    }
  }

  fun hasBackgroundSpanOn(text: String, @ColorRes colorResource: Int): Matcher<View> {
    return object : CustomTypeSafeMatcher<View>("") {
      override fun matchesSafely(view: View?): Boolean {
        if (view == null || view !is TextView)
          return false
        val spannableString = view.text as SpannableString
        val spans = spannableString.getSpans(
          0,
          spannableString.length,
          BackgroundColorSpan::class.java
        )
        for (span in spans) {
          val start = spannableString.getSpanStart(span)
          val end = spannableString.getSpanEnd(span)
          val highlightedString = spannableString.subSequence(start, end)
          if (text == highlightedString.toString()) {
            return span.backgroundColor == view.context.resources.getColor(colorResource)
          }
        }
        return false
      }
    }
  }
}
