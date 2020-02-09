package com.prateekj.snooper.networksnooper.activity

import com.prateekj.snooper.R

enum class HttpCallTab(val tabTitle: Int) {
    RESPONSE(R.string.response),
    REQUEST(R.string.request),
    HEADERS(R.string.headers),
    ERROR(R.string.error)
}
