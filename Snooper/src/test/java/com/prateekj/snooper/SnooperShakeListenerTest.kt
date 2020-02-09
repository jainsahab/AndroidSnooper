package com.prateekj.snooper

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class SnooperShakeListenerTest {

  @Test
  @Throws(Exception::class)
  fun shouldVerifySnooperShakeBehaviour() {
    val shakeAction = mockk<SnooperShakeAction>(relaxed = true)
    val shakeListener = SnooperShakeListener(shakeAction)

    repeat(4) { shakeListener.onShake() }

    verifySequence {
      shakeAction.startSnooperFlow()
      shakeAction.endSnooperFlow()
      shakeAction.startSnooperFlow()
      shakeAction.endSnooperFlow()
    }
  }
}