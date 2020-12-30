package com.prateekj.snooper.customviews

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.view.LayoutInflater.from
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.prateekj.snooper.R

class AccordionView : LinearLayout {

  private var bodyView: View? = null
  private var state: Int = 0
  private var headerText: Int = 0
  private var headerView: View? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initAttributes(attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    initAttributes(attrs)
  }

  @TargetApi(LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
  ) {
    initAttributes(attrs)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    initViews()
  }

  fun onStateChange() {
    bodyView?.visibility = if (state == COLLAPSE) View.GONE else View.VISIBLE
    val statusIcon = headerView!!.findViewById<View>(R.id.state_icon) as ImageView
    statusIcon.setImageResource(if (state == COLLAPSE) R.drawable.arrow_left_white else R.drawable.arrow_down_white)
  }

  private fun initAttributes(attributeSet: AttributeSet) {
    val typedArray =
      context.theme.obtainStyledAttributes(attributeSet, R.styleable.AccordionView, 0, 0)
    state = typedArray.getInteger(R.styleable.AccordionView_state, COLLAPSE)
    headerText = typedArray.getResourceId(
      R.styleable.AccordionView_headerText,
      R.string.accordion_header
    )
    typedArray.recycle()
  }

  private fun initViews() {
    orientation = VERTICAL
    headerView = from(context).inflate(R.layout.accordion_view_heading, this, false)
    (headerView!!.findViewById<View>(R.id.header_text) as TextView).setText(headerText)
    addView(headerView, 0)
    bodyView = findViewWithTag(context.getString(R.string.accordion_body))
    val layoutParams = bodyView!!.layoutParams as MarginLayoutParams
    val accordionViewBodyMargin =
      resources.getDimensionPixelSize(R.dimen.accordion_view_body_margin)
    layoutParams.setMargins(accordionViewBodyMargin, 0, accordionViewBodyMargin, 0)
    onStateChange()
    headerView!!.setOnClickListener {
      toggleState()
      onStateChange()
    }
  }

  private fun toggleState() {
    state = if (state == COLLAPSE) EXPAND else COLLAPSE
  }

  companion object {
    var COLLAPSE = 0
    var EXPAND = 1
  }

}
