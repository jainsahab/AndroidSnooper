package com.prateekj.snooper.okhttp

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

class SnooperInterceptor : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(chain.request())
  }
}
