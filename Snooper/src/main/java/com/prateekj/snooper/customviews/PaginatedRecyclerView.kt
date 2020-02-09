package com.prateekj.snooper.customviews


import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginatedRecyclerView : RecyclerView, PageAddedListener {

  private var listener: NextPageRequestListener? = null
  private var loading: Boolean = false

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
    context,
    attrs,
    defStyle
  )

  override fun setAdapter(adapter: Adapter<*>?) {
    super.setAdapter(adapter)
    adapter!!.registerAdapterDataObserver(AdapterDataAppendObserver(this))
  }

  fun setNextPageListener(listener: NextPageRequestListener) {
    this.listener = listener
  }

  override fun onScrolled(dx: Int, dy: Int) {
    super.onScrolled(dx, dy)
    if (loading || listener == null) {
      return
    }
    if (needToRequestNextPage() && !listener!!.areAllPagesLoaded()) {
      loading = true
      listener!!.requestNextPage()
    }
  }

  private fun needToRequestNextPage(): Boolean {
    val layoutManager = layoutManager as LinearLayoutManager?
    val visibleItemCount = layoutManager!!.childCount
    val totalItemCount = adapter!!.itemCount
    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
    return firstVisibleItemPosition > 0 && firstVisibleItemPosition + visibleItemCount > totalItemCount - 5
  }

  override fun onPageAdded() {
    loading = false
  }
}


