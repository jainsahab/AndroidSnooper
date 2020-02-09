package com.prateekj.snooper.networksnooper.activity

import com.prateekj.snooper.R

import org.junit.Test

import com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class HttpCallTabTest {
    @Test
    fun shouldReturnResponseTabDetails() {
        assertThat(RESPONSE.tabTitle, `is`(R.string.response))
    }

    @Test
    fun shouldReturnRequestTabDetails() {
        assertThat(REQUEST.tabTitle, `is`(R.string.request))
    }

    @Test
    fun shouldReturnHeadersTabDetails() {
        assertThat(HEADERS.tabTitle, `is`(R.string.headers))
    }
}