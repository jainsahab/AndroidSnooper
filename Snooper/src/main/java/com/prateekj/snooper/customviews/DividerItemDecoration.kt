package com.prateekj.snooper.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context, orientation: Int, drawableId: Int) :
  RecyclerView.ItemDecoration() {
  private val mDivider: Drawable? = ContextCompat.getDrawable(context, drawableId)
  private var mOrientation: Int = 0

  init {
    setOrientation(orientation)
  }

  fun setOrientation(orientation: Int) {
    require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "invalid orientation" }
    mOrientation = orientation
  }

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    if (mOrientation == VERTICAL) {
      drawVertical(c, parent)
    } else {
      drawHorizontal(c, parent)
    }
  }

  private fun drawVertical(c: Canvas, parent: RecyclerView) {
    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val params = child
        .layoutParams as RecyclerView.LayoutParams
      val top = child.bottom + params.bottomMargin
      val bottom = top + mDivider!!.intrinsicHeight
      mDivider.setBounds(left, top, right, bottom)
      mDivider.draw(c)
    }
  }

  private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
    val top = parent.paddingTop
    val bottom = parent.height - parent.paddingBottom

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val params = child
        .layoutParams as RecyclerView.LayoutParams
      val left = child.right + params.rightMargin
      val right = left + mDivider!!.intrinsicHeight
      mDivider.setBounds(left, top, right, bottom)
      mDivider.draw(c)
    }
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (mOrientation == VERTICAL) {
      outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
    } else {
      outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
    }
  }

  companion object {
    const val HORIZONTAL = LinearLayoutManager.HORIZONTAL
    const val VERTICAL = LinearLayoutManager.VERTICAL
  }
}
