package com.prateekj.snooper

class SnooperShakeListener(private val shakeAction: SnooperShakeAction) : OnShakeListener {
  private var isSnooperFlowStarted: Boolean = false

  override fun onShake() {
    if (!isSnooperFlowStarted) {
      this.shakeAction.startSnooperFlow()
      isSnooperFlowStarted = true
      return
    }
    this.shakeAction.endSnooperFlow()
    isSnooperFlowStarted = false
  }
}
