package com.prateekj.snooper

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall.Builder
import com.prateekj.snooper.rules.DataResetRule
import com.prateekj.snooper.utils.EspressoUtil.waitFor
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AndroidSnooperTest {

  private lateinit var androidSnooper: AndroidSnooper

  @get:Rule
  var dataResetRule = DataResetRule()

  @Before
  @Throws(Exception::class)
  fun setUp() {
    androidSnooper = AndroidSnooper.instance
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnSameInstanceOnEveryInit() {
    val application = (getInstrumentation() as SnooperInstrumentationRunner).application
    val newSnooper = AndroidSnooper.init(application!!)
    assertThat(newSnooper, sameInstance<AndroidSnooper>(androidSnooper))
  }

  @Test
  @Throws(Exception::class)
  fun shouldSaveHttpCallViaSpringHttpRequestInterceptor() {
    val url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0"
    val responseBody = "responseBody"
    val requestBody = "requestBody"
    val snooperRepo = SnooperRepo(getInstrumentation().targetContext)

    val call = Builder()
      .withUrl(url)
      .withMethod("POST")
      .withPayload(requestBody)
      .withResponseBody(responseBody)
      .withStatusCode(200)
      .withStatusText("OK")
      .build()

    androidSnooper.record(call)

    waitFor { snooperRepo.findAllSortByDate().isNotEmpty() }
    getInstrumentation().runOnMainSync {
      val httpCallRecords = snooperRepo.findAllSortByDate()
      assertThat(httpCallRecords.size, `is`(1))
      val httpCallRecord = httpCallRecords[0]
      assertThat<String>(httpCallRecord.url, `is`(url))
      assertThat<String>(httpCallRecord.payload, `is`(requestBody))
      assertThat<String>(httpCallRecord.method, `is`("POST"))
      assertThat<String>(httpCallRecord.responseBody, `is`(responseBody))
      assertThat(httpCallRecord.statusCode, `is`(200))
      assertThat<String>(httpCallRecord.statusText, `is`("OK"))
    }
  }
}
