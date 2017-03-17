package com.prateekj.snooper.formatter;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class ResponseFormatterFactoryTest {

  private ResponseFormatterFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new ResponseFormatterFactory();
  }

  @Test
  public void shouldReturnXmlFormatter() throws Exception {
    assertTrue(factory.getFor("xml") instanceof XmlFormatter);
  }

  @Test
  public void shouldReturnJsonFormatter() throws Exception {
    assertTrue(factory.getFor("json") instanceof JsonResponseFormatter);
  }

  @Test
  public void shouldReturnPlainTextFormatter() throws Exception {
    assertTrue(factory.getFor("plain") instanceof PlainTextFormatter);
  }
}