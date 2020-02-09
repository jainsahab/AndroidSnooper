package com.prateekj.snooper.formatter

import org.junit.Test

import com.prateekj.snooper.utils.TestUtilities.readFrom
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class PlainTextFormatterTest {

  @Test
  @Throws(Exception::class)
  fun shouldReturnFormattedPlainText() {
    val formatter = PlainTextFormatter()
    val rawString =
      readFrom("person_details_formatted_response.txt").replace("\n".toRegex(), "\r")
    val formattedResponse = formatter.format(rawString)
    val expectedResponse = readFrom("person_details_formatted_response.txt")

    assertThat(formattedResponse, `is`<String>(expectedResponse))
  }
}