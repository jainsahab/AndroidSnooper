package com.prateekj.snooper.customviews

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.widget.ListView

class NonScrollListView : ListView {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  @TargetApi(LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
  )

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val heightMeasureSpecCustom = MeasureSpec.makeMeasureSpec(
      Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST
    )
    super.onMeasure(widthMeasureSpec, heightMeasureSpecCustom)
    val params = layoutParams
    params.height = measuredHeight
  }
}
