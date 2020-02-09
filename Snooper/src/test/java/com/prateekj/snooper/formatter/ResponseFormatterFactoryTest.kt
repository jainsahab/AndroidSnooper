package com.prateekj.snooper.formatter

import org.junit.Before
import org.junit.Test

import junit.framework.Assert.assertTrue

class ResponseFormatterFactoryTest {

  private lateinit var factory: ResponseFormatterFactory

  @Before
  @Throws(Exception::class)
  fun setUp() {
    factory = ResponseFormatterFactory()
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnXmlFormatter() {
    assertTrue(factory.getFor("xml") is XmlFormatter)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnJsonFormatter() {
    assertTrue(factory.getFor("json") is JsonResponseFormatter)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnPlainTextFormatter() {
    assertTrue(factory.getFor("plain") is PlainTextFormatter)
  }
}