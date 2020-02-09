package com.prateekj.snooper.networksnooper.viewmodel

class HttpBodyViewModel {

    var formattedBody: String? = null
        private set

    fun init(formattedBody: String) {
        this.formattedBody = formattedBody
    }
}