package com.prateekj.snooper;

public class SnooperShakeListener implements OnShakeListener {

  private SnooperShakeAction shakeAction;
  private boolean isSnooperFlowStarted;

  public SnooperShakeListener(SnooperShakeAction shakeAction) {
    this.shakeAction = shakeAction;
  }

  @Override
  public void onShake() {
    if (!isSnooperFlowStarted) {
      this.shakeAction.startSnooperFlow();
      isSnooperFlowStarted = true;
      return;
    }
    this.shakeAction.endSnooperFlow();
    isSnooperFlowStarted = false;
  }
}
