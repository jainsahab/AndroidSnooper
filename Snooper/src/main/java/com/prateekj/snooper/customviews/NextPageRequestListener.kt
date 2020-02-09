package com.prateekj.snooper.customviews


interface NextPageRequestListener {
  fun requestNextPage()
  fun areAllPagesLoaded(): Boolean
}
