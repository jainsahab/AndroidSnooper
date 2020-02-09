package com.prateekj.snooper.networksnooper.adapter

import com.prateekj.snooper.networksnooper.model.HttpCallRecord

interface HttpCallListClickListener {
    fun onClick(httpCall: HttpCallRecord)
}
