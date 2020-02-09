package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.ERROR
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.HEADERS
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.REQUEST
import com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE
import com.prateekj.snooper.networksnooper.helper.DataCopyHelper
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.views.HttpCallView
import com.prateekj.snooper.utils.FileUtil
import com.prateekj.snooper.utils.TestUtils.getDate
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class HttpCallPresenterTest {

  private lateinit var view: HttpCallView
  private lateinit var httpCall: HttpCallRecord
  private lateinit var dataCopyHelper: DataCopyHelper
  private lateinit var fileUtil: FileUtil
  private lateinit var backgroundTaskExecutor: BackgroundTaskExecutor
  private lateinit var httpCallPresenter: HttpCallPresenter

  @Before
  @Throws(Exception::class)
  fun setUp() {
    view = mockk(relaxed = true)
    httpCall = mockk(relaxed = true)
    dataCopyHelper = mockk(relaxed = true)
    fileUtil = mockk(relaxed = true)
    backgroundTaskExecutor = mockk(relaxed = true)
    httpCallPresenter = HttpCallPresenter(
      dataCopyHelper,
      httpCall,
      view,
      fileUtil,
      backgroundTaskExecutor
    )
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheResponseData() {
    val responseBody = "response body"
    every { dataCopyHelper.getResponseDataForCopy() } returns responseBody

    httpCallPresenter.copyHttpCallBody(RESPONSE)

    verify { view.copyToClipboard(responseBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheRequestData() {
    val formatRequestBody = "format Request body"
    every { dataCopyHelper.getRequestDataForCopy() } returns formatRequestBody

    httpCallPresenter.copyHttpCallBody(REQUEST)

    verify { view.copyToClipboard(formatRequestBody) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheHeaders() {
    val headers = "headers"
    every { dataCopyHelper.getHeadersForCopy() } returns headers

    httpCallPresenter.copyHttpCallBody(HEADERS)

    verify { view.copyToClipboard(headers) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheError() {
    val error = "error"
    every { dataCopyHelper.getErrorsForCopy() } returns error

    httpCallPresenter.copyHttpCallBody(ERROR)

    verify { view.copyToClipboard(error) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldAskViewToCopyTheEmptyStringIfErrorNotCaptured() {
    every { dataCopyHelper.getErrorsForCopy() } returns null

    httpCallPresenter.copyHttpCallBody(ERROR)

    verify { view.copyToClipboard("") }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShareRequestResponseData() {
    every { httpCall.date } returns getDate(2017, 4, 12, 1, 2, 3)
    val stringBuilder = StringBuilder()
    every { dataCopyHelper.getHttpCallData() } returns stringBuilder
    every {
      fileUtil.createLogFile(
        eq(stringBuilder),
        eq("2017_05_12_01_02_03.txt")
      )
    } returns "filePath"
    resolveBackgroundTask()

    httpCallPresenter.shareHttpCallBody()

    verify { view.shareData("filePath") }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotShareDataIfFileNotCreated() {
    every { httpCall.date } returns getDate(2017, 4, 12, 1, 2, 3)
    val stringBuilder = StringBuilder()
    every { dataCopyHelper.getHttpCallData() } returns stringBuilder
    every {
      fileUtil.createLogFile(
        eq(stringBuilder),
        eq("2017_05_12_01_02_03.txt")
      )
    } returns ""
    resolveBackgroundTask()

    httpCallPresenter.shareHttpCallBody()

    verify(exactly = 0) { view.shareData("filePath") }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowShareNotAvailableDialogWhenPermissionIsDenied() {
    httpCallPresenter.onPermissionDenied()

    verify { view.showMessageShareNotAvailable() }
  }


  private fun resolveBackgroundTask() {
    every { backgroundTaskExecutor.execute(any<BackgroundTask<String>>()) } answers {
      val backgroundTask = this.firstArg<BackgroundTask<String>>()
      backgroundTask.onResult(backgroundTask.onExecute())
    }
  }

}