package com.prateekj.snooper.formatter

import org.junit.Test

import com.prateekj.snooper.utils.TestUtilities.readFrom
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class XmlFormatterTest {

  @Test
  @Throws(Exception::class)
  fun shouldReturnFormattedXmlObject() {
    val formatter = XmlFormatter()
    val formattedResponse = formatter.format(readFrom("person_details_raw_response.xml"))
    val expectedResponse = readFrom("person_details_formatted_response.xml")

    assertThat(formattedResponse, `is`<String>(expectedResponse))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnSameXmlWhenExceptionOccurs() {
    val formatter = XmlFormatter()
    val xml = "<invalid>1</tags>"
    val formattedResponse = formatter.format(xml)

    assertThat(formattedResponse, `is`(xml))
  }
}
