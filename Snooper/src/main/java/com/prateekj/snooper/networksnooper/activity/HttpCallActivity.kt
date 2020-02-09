package com.prateekj.snooper.networksnooper.activity

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.prateekj.snooper.R
import com.prateekj.snooper.formatter.ResponseFormatterFactory
import com.prateekj.snooper.infra.AppPermissionChecker
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import com.prateekj.snooper.networksnooper.database.SnooperRepo
import com.prateekj.snooper.networksnooper.fragment.HttpCallFragment
import com.prateekj.snooper.networksnooper.fragment.HttpHeadersFragment
import com.prateekj.snooper.networksnooper.helper.DataCopyHelper
import com.prateekj.snooper.networksnooper.helper.HttpCallRenderer
import com.prateekj.snooper.networksnooper.presenter.HttpCallPresenter
import com.prateekj.snooper.networksnooper.views.HttpCallView
import com.prateekj.snooper.utils.FileUtil
import kotlinx.android.synthetic.main.activity_http_call_detail.*
import java.io.File

class HttpCallActivity : SnooperBaseActivity(), HttpCallView {
  private var httpCallPresenter: HttpCallPresenter? = null
  private var httpCallRenderer: HttpCallRenderer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_http_call_detail)
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    val httpCallId = intent.getLongExtra(HTTP_CALL_ID, 0)
    val fileUtil = FileUtil()
    val repo = SnooperRepo(this)
    val backgroundTaskExecutor = BackgroundTaskExecutor(this)
    val dataCopyHelper =
      DataCopyHelper(repo.findById(httpCallId), ResponseFormatterFactory(), resources)
    httpCallPresenter = HttpCallPresenter(
      dataCopyHelper,
      repo.findById(httpCallId),
      this,
      fileUtil,
      backgroundTaskExecutor
    )
    val hasError = repo.findById(httpCallId).error != null
    httpCallRenderer = HttpCallRenderer(this, hasError)
    setupUi()
  }

  private fun setupUi() {
    for (tab in httpCallRenderer!!.getTabs()) {
      tab_layout.addTab(tab_layout.newTab().setText(tab.tabTitle))
    }
    tab_layout.tabGravity = TabLayout.GRAVITY_FILL
    val adapter = HttpCallPagerAdapter(supportFragmentManager)
    pager!!.adapter = adapter
    pager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
    tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(tab: Tab) {
        pager!!.currentItem = tab.position
      }

      override fun onTabUnselected(tab: Tab) {

      }

      override fun onTabReselected(tab: Tab) {

      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.http_call_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.copy_menu) {
      val currentTab = httpCallRenderer!!.getTabs()[pager!!.currentItem]
      httpCallPresenter!!.copyHttpCallBody(currentTab)
      return true
    } else if (item.itemId == R.id.share_menu) {
      shareHttpCallData()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun copyToClipboard(data: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied", data)
    clipboard.primaryClip = clip
  }

  override fun shareData(logFilePath: String) {
    val file = File(logFilePath)
    val fileUri =
      FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)
    val intent = Intent(ACTION_SEND)
    intent.setDataAndType(fileUri, LOGFILE_MIME_TYPE)
    intent.putExtra(EXTRA_SUBJECT, getString(R.string.mail_subject_share_logs))
    intent.putExtra(EXTRA_STREAM, fileUri)
    intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    val j = Intent.createChooser(intent, getString(R.string.chooser_title_share_logs))
    startActivity(j)
  }

  override fun showMessageShareNotAvailable() {
    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
  }

  private fun shareHttpCallData() {
    appPermissionChecker.handlePermission(
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      WRITE_EXTERNAL_STORAGE_REQUEST_CODE,
      object : AppPermissionChecker.PermissionRequestCallBack {
        override fun permissionGranted() {
          httpCallPresenter!!.shareHttpCallBody()
        }

        override fun permissionDenied() {
          httpCallPresenter!!.onPermissionDenied()
        }
      })
  }

  override fun getResponseBodyFragment(): Fragment {
    val fragment = HttpCallFragment()
    val extras = intent.extras
    extras!!.putInt(HTTP_CALL_MODE, RESPONSE_MODE)
    fragment.arguments = extras
    return fragment
  }

  override fun getRequestBodyFragment(): Fragment {
    val fragment = HttpCallFragment()
    val extras = intent.extras
    extras!!.putInt(HTTP_CALL_MODE, REQUEST_MODE)
    fragment.arguments = extras
    return fragment
  }

  override fun getHeadersFragment(): Fragment {
    val fragment = HttpHeadersFragment()
    fragment.arguments = intent.extras
    return fragment
  }

  override fun getExceptionFragment(): Fragment {
    val fragment = HttpCallFragment()
    val extras = intent.extras
    extras!!.putInt(HTTP_CALL_MODE, ERROR_MODE)
    fragment.arguments = extras
    return fragment
  }

  private inner class HttpCallPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
      return httpCallRenderer!!.getFragment(position)
    }

    override fun getCount(): Int {
      return httpCallRenderer!!.getTabs().size
    }
  }

  companion object {
    const val HTTP_CALL_ID = "HTTP_CALL_ID"
    const val HTTP_CALL_MODE = "HTTP_CALL_MODE"
    const val REQUEST_MODE = 1
    const val RESPONSE_MODE = 2
    const val ERROR_MODE = 3
    const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
    private const val LOGFILE_MIME_TYPE = "*/*"
  }
}
