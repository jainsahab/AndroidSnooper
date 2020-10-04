package com.prateekj.snooper.networksnooper.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView.BufferType.SPANNABLE
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuItemCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.fragment.app.Fragment
import com.prateekj.snooper.R
import com.prateekj.snooper.formatter.ResponseFormatterFactory
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_ID
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.HTTP_CALL_MODE
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.Bound
import com.prateekj.snooper.networksnooper.presenter.HttpCallFragmentPresenter
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView
import com.prateekj.snooper.utils.Logger
import kotlinx.android.synthetic.main.fragment_response_body.*
import kotlin.math.min

class HttpCallFragment : Fragment(), HttpCallBodyView, OnQueryTextListener, OnScrollChangeListener {
  private var mode: Int = 0
  private lateinit var viewModel: HttpBodyViewModel
  private var presenter: HttpCallFragmentPresenter? = null
  private var lastBoundHighlightedIndex = 0
  private var bounds: List<Bound>? = null
  private var ythPositionOfLastHighlightedBound: Int = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    val view = inflater.inflate(R.layout.fragment_response_body, container, false)
    viewModel = HttpBodyViewModel()
    val repo = SnooperRepo(activity!!)
    val httpCallId = arguments!!.getLong(HTTP_CALL_ID)
    mode = arguments!!.getInt(HTTP_CALL_MODE)
    val taskExecutor = BackgroundTaskExecutor(this.activity!!)
    presenter = HttpCallFragmentPresenter(
      repo,
      httpCallId,
      this,
      ResponseFormatterFactory(),
      taskExecutor
    )
    setHasOptionsMenu(true)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    changeLoaderVisibility(View.VISIBLE)
    presenter!!.init(viewModel, mode)
    scrollView!!.setOnScrollChangeListener(this)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    val searchMenu = menu.findItem(R.id.search_menu)
    searchMenu.isVisible = true
    (MenuItemCompat.getActionView(searchMenu) as SearchView).setOnQueryTextListener(this)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onFormattingDone() {
    val spannableStringBuilder = SpannableStringBuilder(viewModel.formattedBody)
    payload_text!!.setText(spannableStringBuilder, SPANNABLE)
    changeLoaderVisibility(GONE)
  }

  private fun changeLoaderVisibility(visible: Int) {
    embedded_loader.visibility = visible
  }

  override fun highlightBounds(bounds: List<Bound>) {
    this.bounds = bounds
    Logger.d(HttpCallFragment::class.java.simpleName, "Total size: " + bounds.size)
    highlightStringFromBounds(
      bounds.subList(
        lastBoundHighlightedIndex,
        min(BOUNDS_HIGHLIGHT_SET_SIZE, bounds.size)
      )
    )
    scrollTillYOffset(getYthPositionOfBoundInBody(bounds[0]))
  }

  override fun removeOldHighlightedSpans() {
    val spannableString = payload_text!!.text as Spannable
    val backgroundSpans =
      spannableString.getSpans(0, spannableString.length, BackgroundColorSpan::class.java)
    for (span in backgroundSpans) {
      spannableString.removeSpan(span)
    }
  }

  private fun scrollTillYOffset(yOffset: Int) {
    scrollView!!.post { scrollView!!.scrollTo(0, yOffset) }
  }

  override fun onQueryTextSubmit(query: String): Boolean {
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean {
    lastBoundHighlightedIndex = 0
    presenter!!.searchInBody(newText.toLowerCase())
    return true
  }

  override fun onScrollChange(
    v: NestedScrollView,
    scrollX: Int,
    scrollY: Int,
    oldScrollX: Int,
    oldScrollY: Int
  ) {
    if (hasBoundsToHighlight() && needToHighlightNextSetOfBounds(scrollY)) {
      val calculatedToIndex = lastBoundHighlightedIndex + BOUNDS_HIGHLIGHT_SET_SIZE
      highlightStringFromBounds(
        bounds!!.subList(
          lastBoundHighlightedIndex + 1,
          min(calculatedToIndex, bounds!!.size)
        )
      )
    }
  }

  private fun getYthPositionOfBoundInBody(bound: Bound): Int {
    val lineNumber = getLineNumber(bound.left)
    return payload_text!!.layout.getLineTop(lineNumber)
  }

  private fun getLineNumber(indexOfFirstOccurrenceWord: Int): Int {
    return payload_text!!.layout.getLineForOffset(indexOfFirstOccurrenceWord)
  }

  private fun highlightStringFromBounds(bounds: List<Bound>) {
    val text = payload_text!!.text as Spannable
    payload_text!!.postDelayed(getHighlightAction(text, bounds), 5)
  }

  private fun getHighlightAction(text: Spannable, boundsCurrentSet: List<Bound>): Runnable {
    return Runnable {
      for (bound in boundsCurrentSet) {
        text.setSpan(
          BackgroundColorSpan(resources.getColor(R.color.snooper_text_highlight_color)),
          bound.left,
          bound.right,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (boundsCurrentSet.last() == bound) {
          ythPositionOfLastHighlightedBound = getYthPositionOfBoundInBody(bound)
          lastBoundHighlightedIndex = bounds!!.indexOf(bound)
        }
      }
    }
  }


  private fun needToHighlightNextSetOfBounds(scrollY: Int): Boolean {
    return ythPositionOfLastHighlightedBound - scrollY < NEXT_SET_HIGHLIGHT_SCROLL_LINE_BUFFER
  }

  private fun hasBoundsToHighlight(): Boolean {
    return bounds != null && lastBoundHighlightedIndex < bounds!!.size - 1
  }

  companion object {
    const val NEXT_SET_HIGHLIGHT_SCROLL_LINE_BUFFER = 20
    const val BOUNDS_HIGHLIGHT_SET_SIZE = 50
  }
}
