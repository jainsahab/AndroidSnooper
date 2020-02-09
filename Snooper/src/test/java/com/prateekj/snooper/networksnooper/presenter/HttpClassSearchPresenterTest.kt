package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.ArrayList

class HttpClassSearchPresenterTest {

  private lateinit var mockExecutor: BackgroundTaskExecutor
  private lateinit var httpCallSearchView: HttpCallSearchView
  private lateinit var repo: SnooperRepo
  private lateinit var searchPresenter: HttpClassSearchPresenter

  @Before
  @Throws(Exception::class)
  fun setUp() {
    mockExecutor = mockk(relaxed = true)
    httpCallSearchView = mockk(relaxed = true)
    repo = mockk(relaxed = true)
    searchPresenter = HttpClassSearchPresenter(repo, httpCallSearchView, mockExecutor)
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowSearchedHttpCallRecordsOnView() {
    val httpCallRecordList = listOf(mockk<HttpCallRecord>(relaxed = true))
    resolveBackgroundTask()
    every { repo.searchHttpRecord("url") } returns httpCallRecordList

    searchPresenter.searchCalls("url")

    verify { repo.searchHttpRecord("url") }

    verify { httpCallSearchView.hideSearchResultsView() }
    verify { httpCallSearchView.showLoader() }
    verify { httpCallSearchView.hideLoader() }
    verify { httpCallSearchView.showResults(refEq(httpCallRecordList)) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowNoResultsFoundMessage() {
    val httpCallRecordList = ArrayList<HttpCallRecord>()
    resolveBackgroundTask()
    every { repo.searchHttpRecord("url") } returns httpCallRecordList

    searchPresenter.searchCalls("url")

    verify { repo.searchHttpRecord("url") }

    verify { httpCallSearchView.hideSearchResultsView() }
    verify { httpCallSearchView.showLoader() }
    verify { httpCallSearchView.hideLoader() }
    verify { httpCallSearchView.showNoResultsFoundMessage("url") }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowEmptySearchedHttpCallRecordsWhenTextIsEmpty() {
    searchPresenter.searchCalls("")

    verify(exactly = 0) { repo.searchHttpRecord("url") }

    verify { httpCallSearchView.hideLoader() }
    verify { httpCallSearchView.showResults(match { it.size == 0 }) }
    confirmVerified(httpCallSearchView)
  }

  private fun resolveBackgroundTask() {
    every { mockExecutor.execute(any<BackgroundTask<String>>()) } answers {
      val backgroundTask = this.firstArg<BackgroundTask<String>>()
      backgroundTask.onResult(backgroundTask.onExecute())
    }
  }
}