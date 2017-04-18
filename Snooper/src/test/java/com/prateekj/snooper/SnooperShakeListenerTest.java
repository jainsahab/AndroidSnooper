package com.prateekj.snooper;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class SnooperShakeListenerTest {

  @Test
  public void shouldVerifySnooperShakeBehaviour() throws Exception {
    SnooperShakeAction shakeAction = mock(SnooperShakeAction.class);
    InOrder inOrder = Mockito.inOrder(shakeAction);
    SnooperShakeListener shakeListener = new SnooperShakeListener(shakeAction);

    shakeListener.onShake();
    inOrder.verify(shakeAction).startSnooperFlow();

    shakeListener.onShake();
    inOrder.verify(shakeAction).endSnooperFlow();

    shakeListener.onShake();
    inOrder.verify(shakeAction).startSnooperFlow();

    shakeListener.onShake();
    inOrder.verify(shakeAction).endSnooperFlow();
  }
}