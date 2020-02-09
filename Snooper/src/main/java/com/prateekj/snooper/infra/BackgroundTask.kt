package com.prateekj.snooper.infra

interface BackgroundTask<E> {
  fun onExecute(): E
  fun onResult(result: E)
}
