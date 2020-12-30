package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCall
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.views.HttpListView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class HttpCallListPresenterTest {

  private lateinit var view: HttpListView
  private lateinit var repo: SnooperRepo
  private lateinit var httpCallListPresenter: HttpCallListPresenter

  @Before
  @Throws(Exception::class)
  fun setUp() {
    view = mockk(relaxed = true)
    repo = mockk(relaxed = true)
    this.httpCallListPresenter = HttpCallListPresenter(view, repo)
  }

  @Test
  @Throws(Exception::class)
  fun shouldInitializeHttpCallRecordsList() {
    val httpCallRecords = mutableListOf(mockk<HttpCallRecord>(relaxed = true))
    every { repo.findAllSortByDateAfter(-1, 20) } returns httpCallRecords
    httpCallListPresenter.init()

    verify { view.initHttpCallRecordList(httpCallRecords) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowNoCallsFoundMessage() {
    every { repo.findAllSortByDateAfter(-1, 20) } returns mutableListOf()
    httpCallListPresenter.init()

    verify { view.renderNoCallsFoundView() }
    verify(exactly = 0) { view.initHttpCallRecordList(any()) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldQueryNextSetOfRecordOnNextPageCall() {
    val lastSetHttpCalls = mutableListOf(createCallWithId(1))
    val nextSetHttpCalls = mutableListOf(createCallWithId(3), createCallWithId(2))
    every { repo.findAllSortByDateAfter(-1, 20) } returns mutableListOf(
      createCallWithId(5),
      createCallWithId(4)
    )

    httpCallListPresenter.init()

    every { repo.findAllSortByDateAfter(4, 20) } returns nextSetHttpCalls
    httpCallListPresenter.onNextPageCall()
    verify { repo.findAllSortByDateAfter(4, 20) }
    verify { view.appendRecordList(nextSetHttpCalls) }

    every { repo.findAllSortByDateAfter(2, 20) } returns lastSetHttpCalls
    httpCallListPresenter.onNextPageCall()
    verify { repo.findAllSortByDateAfter(2, 20) }
    verify { view.appendRecordList(lastSetHttpCalls) }

    every { repo.findAllSortByDateAfter(1, 20) } returns mutableListOf()
    httpCallListPresenter.onNextPageCall()
    verify { repo.findAllSortByDateAfter(1, 20) }
    verify { view.appendRecordList(match { it.isEmpty() }) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotifyViewToNavigateToResponseBody() {
    val httpCall = mockk<HttpCallRecord>(relaxed = true)
    every { httpCall.id } returns 2L

    httpCallListPresenter.onClick(httpCall)

    verify { view.navigateToResponseBody(2) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldNotifyViewToFinishIt() {
    httpCallListPresenter.onDoneClick()

    verify { view.finishView() }
  }

  @Test
  @Throws(Exception::class)
  fun shouldDeleteTheRecordsAndUpdateUi() {
    httpCallListPresenter.confirmDeleteRecords()

    verify { repo.deleteAll() }
    verify { view.updateListViewAfterDelete() }
  }

  @Test
  @Throws(Exception::class)
  fun shouldShowConfirmationDialogOnClickOfDeleteRecords() {
    httpCallListPresenter.onDeleteRecordsClicked()

    verify { view.showDeleteConfirmationDialog() }
  }

  private fun createCallWithId(id: Int): HttpCallRecord {
    val firstHttpCall = HttpCallRecord.from(HttpCall.Builder().build())
    firstHttpCall.id = id.toLong()
    return firstHttpCall
  }
}