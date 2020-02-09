package com.prateekj.snooper.networksnooper.viewmodel

import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class HttpBodyViewModelTest {

    @Test
    @Throws(Exception::class)
    fun shouldReturnFormattedResponseBodyUsingFormatter() {
        val formattedResponseBody = "formatted payload"
        val httpBodyViewModel = HttpBodyViewModel()
        httpBodyViewModel.init(formattedResponseBody)

        val actualFormattedPayload = httpBodyViewModel.formattedBody

        assertThat<String>(actualFormattedPayload, `is`(formattedResponseBody))
    }
}
