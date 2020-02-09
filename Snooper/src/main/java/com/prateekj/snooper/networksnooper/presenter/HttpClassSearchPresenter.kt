package com.prateekj.snooper.networksnooper.presenter

import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.model.HttpCallRecord
import com.prateekj.snooper.networksnooper.views.HttpCallSearchView

import org.apache.commons.lang3.StringUtils

import java.util.ArrayList

class HttpClassSearchPresenter(
  private val snooperRepo: SnooperRepo,
  private val httpCallSearchView: HttpCallSearchView,
  private val taskExecutor: BackgroundTaskExecutor
) {

  fun searchCalls(text: String) {
    if (text.isEmpty()) {
      showResults(listOf())
      return
    }
    httpCallSearchView.hideSearchResultsView()
    httpCallSearchView.showLoader()
    taskExecutor.execute(searchHttpCallTask(text))
  }

  private fun searchHttpCallTask(text: String): BackgroundTask<List<HttpCallRecord>> {
    return object : BackgroundTask<List<HttpCallRecord>> {
      override fun onExecute(): List<HttpCallRecord> {
        return snooperRepo.searchHttpRecord(text)
      }

      override fun onResult(result: List<HttpCallRecord>) {
        if (result.isEmpty()) {
          showNoResultsFoundMessage(text)
          return
        }
        showResults(result)
      }
    }
  }

  private fun showNoResultsFoundMessage(text: String) {
    httpCallSearchView.hideLoader()
    httpCallSearchView.showNoResultsFoundMessage(text)
  }

  private fun showResults(result: List<HttpCallRecord>) {
    httpCallSearchView.hideLoader()
    httpCallSearchView.showResults(result)
  }
}
