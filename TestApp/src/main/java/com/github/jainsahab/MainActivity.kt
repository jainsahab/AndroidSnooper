package com.github.jainsahab

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prateekj.snooper.AndroidSnooper
import com.prateekj.snooper.okhttp.SnooperInterceptor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException
import java.util.Date


class MainActivity : AppCompatActivity() {

  private val requestBody: JSONObject
    get() {
      val postdata = JSONObject()
        postdata.put("name", "test" + Date().time)
        postdata.put("job", "Investment manager")
        postdata.put("age", "23")
      return postdata
    }

  private val okHttpClient: OkHttpClient
    get() {
      val builder = OkHttpClient.Builder()
      builder.networkInterceptors().add(SnooperInterceptor())
      builder.networkInterceptors().add(object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
          val response = chain.proceed(chain.request())
          return response.newBuilder()
            .removeHeader("Content-type")
            .addHeader("Content-type", "application/json; charset=utf-8")
            .body(
              response.body!!.string()
                .toResponseBody(response.body!!.contentType())
            )
            .build()
        }
      })
      return builder.build()
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidSnooper.init(application)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    return if (id == R.id.action_settings) {
      true
    } else super.onOptionsItemSelected(item)

  }

  fun fetchPosts(view: View) {
    val request = Request.Builder()
      .addHeader("Accept-Encoding", "identity")
      .addHeader("Content-type", "application/json; charset=utf-8")
      .url("https://reqres.in/api/users?page=1&per_page=12")
      .build()
    executeRequest(request)
  }

  fun fetchPostsFail(view: View) {
    val request = Request.Builder()
      .addHeader("Accept-Encoding", "identity")
      .addHeader("Content-type", "application/json; charset=utf-8")
      .url("https://reqres.in/apii/use?page=1&per_page=12")
      .build()
    executeRequest(request)
  }

  fun createPost(view: View) {
    val request = Request.Builder()
      .addHeader("Accept-Encoding", "identity")
      .addHeader("Content-type", "application/json; charset=utf-8")
      .url("https://reqres.in/api/users")
      .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
      .build()
    executeRequest(request)
  }

  private fun executeRequest(request: Request) {
    okHttpClient.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        Log.e("MAIN", "failure $e", e)
      }

      @Throws(IOException::class)
      override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
          val text = response.body!!.string()
          runOnUiThread {
            result.text = text
          }
        }
      }
    })
  }
}
