package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.formatter.ResponseFormatter
import com.prateekj.snooper.formatter.ResponseFormatterFactory
import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.ERROR_MODE
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.REQUEST_MODE
import com.prateekj.snooper.networksnooper.activity.HttpCallActivity.Companion.RESPONSE_MODE
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.model.HttpHeader
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class HttpCallFragmentPresenterTest {

  private lateinit var presenter: HttpCallFragmentPresenter
  private lateinit var repo: SnooperRepo
  private lateinit var viewModel: HttpBodyViewModel
  private lateinit var httpCallRecord: HttpCallRecord
  private lateinit var factory: ResponseFormatterFactory
  private lateinit var responseFormatter: ResponseFormatter
  private lateinit var responseBody: String
  private lateinit var requestPayload: String
  private lateinit var error: String
  private lateinit var formattedBody: String
  private lateinit var mockExecutor: BackgroundTaskExecutor
  private lateinit var httpCallBodyView: HttpCallBodyView

  private val jsonContentTypeHeader: HttpHeader
    get() {
      val headerValue = HttpHeaderValue("application/json")
      val httpHeader = HttpHeader("Content-Type")
      httpHeader.values = listOf(headerValue)
      return httpHeader
    }

  private val xmlContentTypeHeader: HttpHeader
    get() {
      val headerValue = HttpHeaderValue("application/xml")
      val httpHeader = HttpHeader("Content-Type")
      httpHeader.values = listOf(headerValue)
      return httpHeader
    }

  @Before
  @Throws(Exception::class)
  fun setUp() {
    responseBody = "response body"
    requestPayload = "payload"
    error = "error"
    formattedBody = "formatted body"
    httpCallRecord = mockk(relaxed = true)
    repo = mockk(relaxed = true)
    viewModel = mockk(relaxed = true)
    factory = mockk(relaxed = true)
    mockExecutor = mockk(relaxed = true)
    httpCallBodyView = mockk(relaxed = true)
    presenter = HttpCallFragmentPresenter(
      repo,
      HTTP_CALL_ID,
      httpCallBodyView,
      factory,
      mockExecutor
    )
    responseFormatter = mockk()
    every { httpCallRecord.responseBody } returns responseBody
    every { httpCallRecord.responseBody }.returns(responseBody)
    every { httpCallRecord.payload }.returns(requestPayload)
    every { httpCallRecord.error }.returns(error)
    every { repo.findById(HTTP_CALL_ID) }.returns(httpCallRecord)
    every { factory.getFor(any()) }.returns(responseFormatter)
    every { responseFormatter.format(any()) }.returns(formattedBody)
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeWithJsonFormatterForResponseMode() {
    val httpHeader = jsonContentTypeHeader
    every { httpCallRecord.getResponseHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, RESPONSE_MODE)

    verify { factory.getFor("application/json") }
    verify { responseFormatter.format(responseBody) }
    verify { viewModel.init(formattedBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeWithXmlFormatterForResponseMode() {
    val httpHeader = xmlContentTypeHeader
    every { httpCallRecord.getResponseHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, RESPONSE_MODE)

    verify { factory.getFor("application/xml") }
    verify { responseFormatter.format(responseBody) }
    verify { viewModel.init(formattedBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeWithErrorContent() {
    every { httpCallRecord.getResponseHeader("Content-Type") } returns null
    resolveBackgroundTask()

    presenter.init(viewModel, ERROR_MODE)

    verify { viewModel.init(error) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeWithJsonFormatterForRequestMode() {
    val httpHeader = jsonContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    verify { factory.getFor("application/json") }
    verify { responseFormatter.format(requestPayload) }
    verify { viewModel.init(formattedBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeWithXmlFormatterForRequestMode() {
    val httpHeader = xmlContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    verify { factory.getFor("application/xml") }
    verify { responseFormatter.format(requestPayload) }
    verify { viewModel.init(formattedBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldUsePlainTextFormatterWhenContentTypeHeaderNotFound() {
    every { httpCallRecord.getRequestHeader("Content-Type") } returns (null)
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    verify { factory wasNot Called }
    verify { viewModel.init(requestPayload) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotifyViewOnFormattingDone() {
    val httpHeader = xmlContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    verify { viewModel.init(any()) }
    verify { httpCallBodyView.onFormattingDone() }
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnBoundsToHighlight() {
    every { responseFormatter.format(any()) } returns "ABC0124abc"
    val httpHeader = jsonContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    presenter.searchInBody("abc")

    verify { httpCallBodyView.removeOldHighlightedSpans() }
    verify {
      httpCallBodyView.highlightBounds(match { item ->
        val firstBound = item[0]
        assertThat(firstBound.left, `is`(0))
        assertThat(firstBound.right, `is`(3))
        val secondBound = item[1]
        assertThat(secondBound.left, `is`(7))
        assertThat(secondBound.right, `is`(10))
        true
      })
    }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotHighlightSpansWhenPatternIsEmpty() {
    every { responseFormatter.format(any()) } returns "ABC0124abc"
    val httpHeader = jsonContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    presenter.searchInBody("")

    verify { httpCallBodyView.removeOldHighlightedSpans() }
    verify(exactly = 0) { httpCallBodyView.highlightBounds(any()) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotHighlightSpansWhenPatternNotFound() {
    every { responseFormatter.format(any()) } returns "ABC0124abc"
    val httpHeader = jsonContentTypeHeader
    every { httpCallRecord.getRequestHeader("Content-Type") } returns httpHeader
    resolveBackgroundTask()
    presenter.init(viewModel, REQUEST_MODE)

    presenter.searchInBody("789")

    verify { httpCallBodyView.removeOldHighlightedSpans() }
    verify(exactly = 0) { httpCallBodyView.highlightBounds(any()) }
  }

  private fun resolveBackgroundTask() {
    every { mockExecutor.execute(any<BackgroundTask<String>>()) } answers {
      val backgroundTask = this.firstArg<BackgroundTask<String>>()
      backgroundTask.onResult(backgroundTask.onExecute())
    }
  }

  companion object {
    const val HTTP_CALL_ID: Long = 5
  }
}
